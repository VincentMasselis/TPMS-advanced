package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.content.Context
import android.os.Build
import co.touchlab.kermit.Logger
import com.masselis.tpmsadvanced.core.common.dematerializeCompletion
import com.masselis.tpmsadvanced.core.common.materializeCompletion
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.Companion.INDICATE_UUID
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoType
import java.util.UUID.fromString
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

/**
 * Decorates a [BluetoothLeScanner] to read the measurements of the Tesla TPMS sensors.
 *
 * A Tesla sensor never advertises its measurements, only its presence (see [RawTeslaIdOnly]). Every
 * [ScannerRecord.TeslaId] is forwarded as is, and in parallel, a GATT connection is opened to the
 * sensor and kept open. Once the indications are enabled, the sensor sends its measurements by
 * itself as `TPData` indications, each one is emitted as a [Tyre.Unlocated] with the same sensor ID.
 * The sensor sends `TPData` when the indications are enabled and when it moves, a static sensor only
 * sends a `ConnectionMetrics` every 4 minutes. This heartbeat re-emits the last measurement with a
 * new timestamp since the connection is still alive.
 *
 * Nothing is written to the sensor except the descriptor which enables the indications: an
 * unpaired sensor ignores the requests the car sends (`TPDataRequest`, `GenealogyRequest`).
 *
 * Once the connection is lost, the sensor advertises again and its next [ScannerRecord.TeslaId]
 * opens a new connection.
 *
 * The sensor accepts a single connection at once, while the phone is connected, the car can't read
 * it.
 *
 * The messages come from the VCSEC protocol buffers definitions, see
 * [trifinite/vcsec-archive](https://github.com/trifinite/vcsec-archive). Unlike the messages
 * exchanged with the Tesla app, they are not prefixed by their size.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
@Suppress("MagicNumber", "MissingPermission")
internal class TeslaGatt(
    private val context: Context,
    private val source: BluetoothLeScanner,
) : BluetoothLeScanner by source {

    private val logger = Logger.withTag("TeslaGatt")

    // Shared by both scans so a sensor never gets two connections at the same time
    private val connectedSensors = ConcurrentHashMap.newKeySet<Int>()

    private val highDutyScanFlow = source.highDutyScan().readTeslaSensors().shared()

    override fun highDutyScan(): Flow<ScannerRecord> = highDutyScanFlow

    private val normalScanFlow = source.normalScan().readTeslaSensors().shared()

    override fun normalScan(): Flow<ScannerRecord> = normalScanFlow

    // The connections are bound to the collection, they are closed once the scan is not collected
    // anymore
    private fun Flow<ScannerRecord>.readTeslaSensors(): Flow<ScannerRecord> = channelFlow {
        collect { record ->
            send(record)
            if (record is ScannerRecord.TeslaId && connectedSensors.add(record.sensorId)) launch {
                record
                    .connection()
                    .catch { logger.w(it) { "Connection lost with the Tesla sensor ${record.device.address}" } }
                    .onCompletion { connectedSensors.remove(record.sensorId) }
                    .collect { send(it) }
            }
        }
    }

    // Emits every reading sent by the sensor until the connection is lost, which ends with an
    // exception
    private fun ScannerRecord.TeslaId.connection(): Flow<Tyre.Unlocated> = flow {
        val events = Channel<GattEvent>(Channel.UNLIMITED)
        // The scanned device is used instead of rebuilding it from its address since it holds the
        // address type the connection requires: OEM sensors use a public address but some clones use
        // a random static one
        val gatt = device
            .connectGatt(context, false, GattEvent.Callback(events), TRANSPORT_LE)
            .let { checkNotNull(it) { "Unable to connect" } }
        try {
            // A sensor which accepts the connection answers within a second or two, waiting longer
            // mostly means an other device, like the car, is already connected to it
            withTimeout(CONNECTION_TIMEOUT) { events.await<GattEvent.Connected>() }
            withTimeout(SETUP_TIMEOUT) {
                check(gatt.discoverServices()) { "Unable to discover services" }
                events.await<GattEvent.ServicesDiscovered>()
                checkNotNull(gatt.getService(SERVICE_UUID)) { "Tesla service not found" }
                    .getCharacteristic(INDICATE_UUID)
                    .let { checkNotNull(it) { "Indicate characteristic not found" } }
                    .also { check(gatt.setCharacteristicNotification(it, true)) { "Unable to enable indications" } }
                    .getDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID)
                    .let { checkNotNull(it) { "Indicate descriptor not found" } }
                    .also { gatt.writeIndicationDescriptor(it) }
                events.await<GattEvent.DescriptorWritten>()
            }
            // No timeout, the connection lasts until the sensor or the phone drops it. A dropped
            // connection is received as a `GattEvent.Failure` which throws.
            var lastData: TPData? = null
            while (true) events
                .await<GattEvent.Indication>()
                .value
                // Diagnostic: logs every message, even the ones which are not decoded
                .also { logger.d { "Indication: ${it.toHexString()}, decoded: ${it.decode()}" } }
                .decode()
                ?.unsignedMessage
                ?.let { message ->
                    message.tpData
                        ?.also { lastData = it }
                        // The heartbeat confirms the last measurement is still the current one
                        ?: lastData?.takeIf { message.connectionMetrics != null }
                }
                ?.let { data ->
                    Tyre.Unlocated(
                        now(),
                        rssi, // Measured on the advertisement which triggered the connection
                        sensorId,
                        data.gaugePressure(),
                        data.temperature.toFloat().celsius,
                        0u, // The battery is not sent by the sensor
                        false, // The alarms are not read
                    )
                }
                ?.also { emit(it) }
        } finally {
            gatt.disconnect()
            gatt.close()
        }
    }

    @Suppress("DEPRECATION")
    private fun BluetoothGatt.writeIndicationDescriptor(descriptor: BluetoothGattDescriptor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            check(writeDescriptor(descriptor, ENABLE_INDICATION_VALUE) == GATT_SUCCESS) {
                "Unable to enable indications"
            }
        else
            descriptor
                .also { it.value = ENABLE_INDICATION_VALUE }
                .let { check(writeDescriptor(it)) { "Unable to enable indications" } }
    }

    // Returns the next event of type [T], the events of an other type are skipped
    private suspend inline fun <reified T : GattEvent> ReceiveChannel<GattEvent>.await(): T {
        while (true) when (val event = receive()) {
            is T -> return event
            is GattEvent.Failure -> error(event.message)
            else -> logger.v { "Event skipped while waiting for ${T::class.simpleName}: $event" }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun Flow<ScannerRecord>.shared() = this
        .materializeCompletion()
        .shareIn(GlobalScope + Dispatchers.Default, WhileSubscribed())
        .dematerializeCompletion()

    /** Converts the callbacks of [BluetoothGattCallback] into events, only successes are sent. */
    private sealed interface GattEvent {
        data object Connected : GattEvent
        data object ServicesDiscovered : GattEvent
        data object DescriptorWritten : GattEvent
        class Indication(val value: ByteArray) : GattEvent {
            override fun toString(): String = "Indication(${value.toHexString()})"
        }
        class Failure(val message: String) : GattEvent

        class Callback(private val events: SendChannel<GattEvent>) : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                events.trySend(
                    if (status == GATT_SUCCESS && newState == STATE_CONNECTED) Connected
                    else Failure("Disconnected, status: $status, state: $newState")
                )
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                events.trySend(
                    if (status == GATT_SUCCESS) ServicesDiscovered
                    else Failure("Services discovery failed, status: $status")
                )
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                events.trySend(
                    if (status == GATT_SUCCESS) DescriptorWritten
                    else Failure("Descriptor write failed, status: $status")
                )
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                events.trySend(Indication(value))
            }

            // Only called below Android 13, since Android 13 the overload above is called instead
            @Deprecated("Deprecated in Java")
            @Suppress("DEPRECATION")
            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                    events.trySend(Indication(characteristic.value))
            }
        }
    }

    // Subset of the VCSEC protocol buffers definitions, the unknown fields are skipped while decoding

    /** Sensor → car, received on [INDICATE_UUID] */
    @Serializable
    internal data class FromTPWheelUnitMessage(
        @ProtoNumber(2) val unsignedMessage: UnsignedMessageTPWheelUnit? = null,
    )

    @Serializable
    internal data class UnsignedMessageTPWheelUnit(
        @ProtoNumber(28) val tpData: TPData? = null,
        @ProtoNumber(34) val connectionMetrics: ConnectionMetrics? = null,
    )

    @Serializable
    internal data class TPData(
        @ProtoNumber(1) val pressure: Int = 0,
        @ProtoNumber(2) @ProtoType(ProtoIntegerType.SIGNED) val temperature: Int = 0,
    ) {
        // The pressure is absolute, an unmounted sensor reads `100`. Removing one standard atmosphere
        // returns the gauge pressure, checked against a gauge on 4 sensors by
        // https://github.com/anufryieu/tesla-tpms-ble/blob/main/docs/PROTOCOL.md. Clamped because the
        // atmospheric pressure is often slightly below the standard one.
        fun gaugePressure() = pressure
            .toFloat()
            .minus(101.325f)
            .coerceAtLeast(0f)
            .kpa
    }

    /** Sent every 4 minutes by the sensor while connected */
    @Serializable
    internal data class ConnectionMetrics(
        @ProtoNumber(1) val goodConnEventCount: Int = 0,
        @ProtoNumber(2) val missedConnEventCount: Int = 0,
        @ProtoNumber(3) val badCRCConnEventCount: Int = 0,
    )

    internal companion object {
        private val SERVICE_UUID = fromString("00000211-b2d1-43f0-9b88-960cebf8b91e")
        private val INDICATE_UUID = fromString("00000213-b2d1-43f0-9b88-960cebf8b91e")
        private val CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = fromString("00002902-0000-1000-8000-00805f9b34fb")

        private val CONNECTION_TIMEOUT = 5.seconds
        private val SETUP_TIMEOUT = 5.seconds

        // Returns null if the content is not a valid message
        fun ByteArray.decode(): FromTPWheelUnitMessage? = try {
            ProtoBuf.decodeFromByteArray<FromTPWheelUnitMessage>(this)
        } catch (_: SerializationException) {
            null
        }
    }
}

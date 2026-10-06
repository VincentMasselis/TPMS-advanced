package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
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
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.Companion.POLL_INTERVAL
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.Companion.WRITE_UUID
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.GenealogyRequest.GENEALOGYREQUEST_TPWHEELUNITINFO_READ
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.TeslaGatt.TPDataRequest.TP_DATAREQUEST_PRESSURE_TEMPERATURE
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
import kotlinx.coroutines.delay
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
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoType
import java.util.UUID.fromString
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

/**
 * Decorates a [BluetoothLeScanner] to read the measurements of the Tesla TPMS sensors.
 *
 * A Tesla sensor never advertises its measurements, only its presence (see [RawTeslaIdOnly]). Every
 * [ScannerRecord.TeslaId] is forwarded as is, and in parallel, a GATT connection is opened to the
 * sensor and kept open. While connected, the sensor is asked for its measurements every
 * [POLL_INTERVAL], the same way the car does: it writes a `TPDataRequest` and the sensor answers a
 * `TPData` indication. Each answer is emitted as a [Tyre.Unlocated] with the same sensor ID.
 *
 * Once the connection is lost, the sensor advertises again and its next [ScannerRecord.TeslaId]
 * opens a new connection.
 *
 * Only read requests are sent, nothing is enrolled, bonded or configured on the sensor.
 *
 * The sensor accepts a single connection at once, while the phone is connected, the car can't read
 * it.
 *
 * The messages come from the VCSEC protocol buffers definitions, see
 * [trifinite/vcsec-archive](https://github.com/trifinite/vcsec-archive). Every message is prefixed
 * by its size, as a 2 bytes big-endian integer.
 */
@OptIn(ExperimentalSerializationApi::class)
@Suppress("TooManyFunctions", "MagicNumber", "MissingPermission")
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

    // Emits a reading every [POLL_INTERVAL] until the connection is lost, which ends with an exception
    private fun ScannerRecord.TeslaId.connection(): Flow<Tyre.Unlocated> = flow {
        val events = Channel<GattEvent>(Channel.UNLIMITED)
        // The scanned device is used instead of rebuilding it from its address since it holds the
        // address type (public or random) the connection requires
        val gatt = device
            .connectGatt(context, false, GattEvent.Callback(events), TRANSPORT_LE)
            .let { checkNotNull(it) { "Unable to connect" } }
        try {
            // A sensor which accepts the connection answers within a second or two, waiting longer
            // mostly means an other device, like the car, is already connected to it
            withTimeout(CONNECTION_TIMEOUT) { events.await<GattEvent.Connected>() }
            val write = withTimeout(REQUEST_TIMEOUT) {
                check(gatt.discoverServices()) { "Unable to discover services" }
                events.await<GattEvent.ServicesDiscovered>()
                val service = checkNotNull(gatt.getService(SERVICE_UUID)) { "Tesla service not found" }
                service.getCharacteristic(INDICATE_UUID)
                    .let { checkNotNull(it) { "Indicate characteristic not found" } }
                    .also { check(gatt.setCharacteristicNotification(it, true)) { "Unable to enable indications" } }
                    .getDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID)
                    .let { checkNotNull(it) { "Indicate descriptor not found" } }
                    .also { gatt.writeIndicationDescriptor(it) }
                events.await<GattEvent.DescriptorWritten>()
                checkNotNull(service.getCharacteristic(WRITE_UUID)) { "Write characteristic not found" }
            }
            // Read once per connection since the battery voltage barely changes. It's not mandatory,
            // a sensor which doesn't answer still returns its pressure.
            val battery = withTimeoutOrNull(BATTERY_TIMEOUT) {
                gatt.request(
                    write,
                    events,
                    ToTPWheelUnitMessage(genealogyRequest = GENEALOGYREQUEST_TPWHEELUNITINFO_READ)
                ) { it.tpWheelUnitInfo }
            }
                ?.batteryVoltageMv
                ?.div(100f)
                ?.roundToInt()
                ?.toUShort() // Returns 30 for 3.0 volts
                ?: 0u // The battery couldn't be read
            while (true) {
                withTimeout(REQUEST_TIMEOUT) {
                    gatt.request(
                        write,
                        events,
                        ToTPWheelUnitMessage(tpDataRequest = TP_DATAREQUEST_PRESSURE_TEMPERATURE)
                    ) { it.tpData }
                }.let { data ->
                    Tyre.Unlocated(
                        now(),
                        rssi, // Measured on the advertisement which triggered the connection
                        sensorId,
                        data.gaugePressure(),
                        data.temperature.toFloat().celsius,
                        battery,
                        false, // The alarms are not read
                    )
                }.also { emit(it) }
                delay(POLL_INTERVAL)
            }
        } finally {
            gatt.disconnect()
            gatt.close()
        }
    }

    private suspend fun <T : Any> BluetoothGatt.request(
        characteristic: BluetoothGattCharacteristic,
        events: ReceiveChannel<GattEvent>,
        message: ToTPWheelUnitMessage,
        select: (UnsignedMessageTPWheelUnit) -> T?,
    ): T {
        writeCharacteristicCompat(characteristic, message.frame())
        // The sensor could also send other messages, they are ignored until the expected one comes
        while (true) events
            .await<GattEvent.Indication>()
            .value
            .unframe()
            ?.unsignedMessage
            ?.let(select)
            ?.let { return it }
    }

    @Suppress("DEPRECATION")
    private fun BluetoothGatt.writeCharacteristicCompat(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            check(writeCharacteristic(characteristic, value, WRITE_TYPE_DEFAULT) == GATT_SUCCESS) {
                "Unable to write the request"
            }
        else
            characteristic
                .also { it.writeType = WRITE_TYPE_DEFAULT }
                .also { it.value = value }
                .let { check(writeCharacteristic(it)) { "Unable to write the request" } }
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
            else -> Unit
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
        data object CharacteristicWritten : GattEvent
        class Indication(val value: ByteArray) : GattEvent
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

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                events.trySend(
                    if (status == GATT_SUCCESS) CharacteristicWritten
                    else Failure("Characteristic write failed, status: $status")
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

    /** Car → sensor, written on [WRITE_UUID]. Proto3 `oneof`, only one field must be set. */
    @Serializable
    internal data class ToTPWheelUnitMessage(
        @ProtoNumber(31) val genealogyRequest: GenealogyRequest? = null,
        @ProtoNumber(35) val tpDataRequest: TPDataRequest? = null,
    )

    @Serializable
    internal enum class TPDataRequest {
        @ProtoNumber(1)
        TP_DATAREQUEST_PRESSURE_TEMPERATURE,
    }

    @Serializable
    internal enum class GenealogyRequest {
        @ProtoNumber(3)
        GENEALOGYREQUEST_TPWHEELUNITINFO_READ,
    }

    /** Sensor → car, received on [INDICATE_UUID] */
    @Serializable
    internal data class FromTPWheelUnitMessage(
        @ProtoNumber(2) val unsignedMessage: UnsignedMessageTPWheelUnit? = null,
    )

    @Serializable
    internal data class UnsignedMessageTPWheelUnit(
        @ProtoNumber(28) val tpData: TPData? = null,
        @ProtoNumber(29) val tpWheelUnitInfo: TPWheelUnitInfo? = null,
    )

    @Serializable
    internal data class TPData(
        @ProtoNumber(1) val pressure: Int = 0,
        @ProtoNumber(2) @ProtoType(ProtoIntegerType.SIGNED) val temperature: Int = 0,
    ) {
        // `101` is the only value published, from a sensor on a bench, so the pressure is probably
        // absolute like the advertisement's one. Removing 100 kPa (~1 atm) returns the gauge
        // pressure. Still has to be checked against a reference gauge.
        fun gaugePressure() = pressure
            .minus(100)
            .coerceAtLeast(0)
            .toFloat()
            .kpa
    }

    @Serializable
    internal data class TPWheelUnitInfo(
        @ProtoNumber(3) val batteryVoltageMv: Int = 0,
    )

    internal companion object {
        private val SERVICE_UUID = fromString("00000211-b2d1-43f0-9b88-960cebf8b91e")
        private val WRITE_UUID = fromString("00000212-b2d1-43f0-9b88-960cebf8b91e")
        private val INDICATE_UUID = fromString("00000213-b2d1-43f0-9b88-960cebf8b91e")
        private val CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = fromString("00002902-0000-1000-8000-00805f9b34fb")

        private val CONNECTION_TIMEOUT = 5.seconds
        private val REQUEST_TIMEOUT = 5.seconds

        // The sensor is said to drop an idle connection after ~15 seconds (unverified), polling
        // more often keeps the connection alive
        private val POLL_INTERVAL = 10.seconds
        private val BATTERY_TIMEOUT = 3.seconds

        fun ToTPWheelUnitMessage.frame(): ByteArray = ProtoBuf
            .encodeToByteArray(this)
            .let { byteArrayOf((it.size shr 8).toByte(), it.size.toByte()) + it }

        // Returns null if the size prefix doesn't match or if the content is not a valid message
        fun ByteArray.unframe(): FromTPWheelUnitMessage? = this
            .takeIf { it.size >= 2 }
            ?.takeIf { (((it[0].toInt() and 0xFF) shl 8) or (it[1].toInt() and 0xFF)) == it.size - 2 }
            ?.copyOfRange(2, size)
            ?.let {
                try {
                    ProtoBuf.decodeFromByteArray<FromTPWheelUnitMessage>(it)
                } catch (_: SerializationException) {
                    null
                }
            }
    }
}

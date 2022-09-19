package com.masselis.tpmsadvanced.data.record.interfaces

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.MATCH_MODE_AGGRESSIVE
import android.bluetooth.le.ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import androidx.core.util.size
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.record.ioc.SingleInstance
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.record.model.Tyre
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID.fromString
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@SuppressLint("MissingPermission")
@SingleInstance
internal class BluetoothLeScannerImpl @Inject internal constructor() : BluetoothLeScanner {

    @SuppressLint("InlinedApi")
    @RequiresPermission("android.permission.BLUETOOTH_SCAN")
    private fun scan(mode: Int) = callbackFlow {
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                launch { send(result) }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                launch { results.forEach { send(it) } }
            }

            override fun onScanFailed(errorCode: Int) {
                close(ScanFailed(errorCode))
            }
        }
        val leScanner = appContext
            .getSystemService<BluetoothManager>()!!
            .adapter
            .bluetoothLeScanner
        leScanner.startScan(
            listOf(
                ScanFilter
                    .Builder()
                    .setServiceUuid(SYSGRATION_SERVICE_UUID)
                    .build()
            ),
            ScanSettings
                .Builder()
                .setScanMode(mode)
                .setMatchMode(MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(MATCH_NUM_ONE_ADVERTISEMENT)
                .build(),
            callback
        )
        awaitClose {
            leScanner.flushPendingScanResults(callback)
            leScanner.stopScan(callback)
        }
    }.flowOn(Dispatchers.Main) // System's BluetoothLeScanner class as issues if called on a background thread
        .mapNotNull { result -> result.scanRecord?.manufacturerSpecificData?.takeIf { it.size > 0 } }
        .map { Raw(it.valueAt(0)) }
        .filter { it.address().contentEquals(expectedAddress) }

    class ScanFailed(val reason: Int) : Exception()

    private val lowLatencyScanFlow = scan(ScanSettings.SCAN_MODE_LOW_LATENCY).shared()

    override fun highDutyScan(): Flow<Tyre> = lowLatencyScanFlow

    @SuppressLint("MissingPermission")
    private val balancedScanFlow = scan(ScanSettings.SCAN_MODE_BALANCED).shared()

    override fun normalScan(): Flow<Tyre> = balancedScanFlow

    private fun Flow<Raw>.shared() = this
        .shareIn(
            CoroutineScope(EmptyCoroutineContext),
            // Anti-spam mechanism to avoid an exception when requesting 6 scans within a 30s frame
            SharingStarted.WhileSubscribed(5.seconds, Duration.ZERO)
        )
        .map { raw ->
            @Suppress("MagicNumber")
            Tyre(
                now(),
                TyreLocation.values().first { it.byte == raw.location() },
                ByteBuffer
                    .wrap(byteArrayOf(0x00) + raw.id())
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .int,
                ByteBuffer
                    .wrap(raw.pressure())
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .int
                    .div(1000f)
                    .kpa,
                ByteBuffer
                    .wrap(raw.temperature())
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .int
                    .div(100f)
                    .celsius,
                raw.battery().toInt().toUShort(),
                raw.alarm() == PRESSURE_ALARM_BYTE
            )
        }

    @JvmInline
    @Suppress("MagicNumber")
    internal value class Raw(private val bytes: ByteArray) {
        fun location(): UByte = bytes[0].toUByte()
        fun address(): ByteArray = bytes.copyOfRange(1, 3)
        fun id(): ByteArray = bytes.copyOfRange(3, 6)
        fun pressure(): ByteArray = bytes.copyOfRange(6, 10)
        fun temperature(): ByteArray = bytes.copyOfRange(10, 14)
        fun battery(): Byte = bytes[14]
        fun alarm(): Byte = bytes[15]
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    companion object {
        private val SYSGRATION_SERVICE_UUID = ParcelUuid(
            fromString("0000fbb0-0000-1000-8000-00805f9b34fb")
        )
        private val expectedAddress = ubyteArrayOf(0xEAu, 0xCAu).toByteArray()
        private const val PRESSURE_ALARM_BYTE = 0x01.toByte()
    }
}
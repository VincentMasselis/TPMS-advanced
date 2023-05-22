package com.masselis.tpmsadvanced.data.record.interfaces

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.bluetooth.BluetoothAdapter.ERROR
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.MATCH_MODE_AGGRESSIVE
import android.bluetooth.le.ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.getSystemService
import androidx.core.util.size
import com.masselis.tpmsadvanced.core.common.asFlow
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.record.ioc.DataRecordComponent
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.record.model.Tyre
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID.fromString
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@SuppressLint("MissingPermission")
@DataRecordComponent.Scope
internal class BluetoothLeScannerImpl @Inject internal constructor(
    private val context: Context
) : BluetoothLeScanner {

    private val bluetoothAdapter get() = context.getSystemService<BluetoothManager>()?.adapter

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
                close(BluetoothLeScanner.ScanFailed(errorCode))
            }
        }
        val leScanner = bluetoothAdapter!!.bluetoothLeScanner
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

    private val lowLatencyScanFlow = scan(ScanSettings.SCAN_MODE_LOW_LATENCY).shared()

    override fun highDutyScan(): Flow<Tyre> = lowLatencyScanFlow

    @SuppressLint("MissingPermission")
    private val balancedScanFlow = scan(ScanSettings.SCAN_MODE_BALANCED).shared()

    override fun normalScan(): Flow<Tyre> = balancedScanFlow

    private fun Flow<Raw>.shared() = this
        .map { raw ->
            @Suppress("MagicNumber")
            Tyre(
                now(),
                SensorLocation.values().first { it.byte == raw.location() },
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
        .shareIn(
            CoroutineScope(Dispatchers.Default),
            // Anti-spam mechanism to avoid an exception when requesting 6 scans within a 30s frame
            SharingStarted.WhileSubscribed(5.seconds, Duration.ZERO),
            0
        )

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

    @SuppressLint("InlinedApi")
    @Suppress("MagicNumber")
    override fun missingPermission(): List<String> = when (Build.VERSION.SDK_INT) {
        in Int.MIN_VALUE..28 -> listOf(ACCESS_COARSE_LOCATION)
        in 29..30 -> listOf(ACCESS_FINE_LOCATION)
        in 31..Int.MAX_VALUE -> listOf(BLUETOOTH_CONNECT, BLUETOOTH_SCAN)
        else ->
            @Suppress("ThrowingExceptionsWithoutMessageOrCause")
            throw IllegalArgumentException()
    }.filter { checkSelfPermission(context, it) != PERMISSION_GRANTED }

    override fun isChipTurnedOn(): Flow<Boolean> = IntentFilter(ACTION_STATE_CHANGED)
        .asFlow()
        .map { it.getIntExtra(EXTRA_STATE, ERROR) }
        .filter { it != ERROR }
        .map { it == STATE_ON }
        .onStart { emit(bluetoothAdapter?.isEnabled ?: false) }

    @OptIn(ExperimentalUnsignedTypes::class)
    companion object {
        private val SYSGRATION_SERVICE_UUID = ParcelUuid(
            fromString("0000fbb0-0000-1000-8000-00805f9b34fb")
        )
        private val expectedAddress = ubyteArrayOf(0xEAu, 0xCAu).toByteArray()
        private const val PRESSURE_ALARM_BYTE = 0x01.toByte()
    }
}

package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.MATCH_MODE_AGGRESSIVE
import android.bluetooth.le.ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.getSystemService
import androidx.core.util.size
import com.masselis.tpmsadvanced.core.common.dematerializeCompletion
import com.masselis.tpmsadvanced.core.common.materializeCompletion
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.UUID.fromString
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@SuppressLint("MissingPermission")
@DataVehicleComponent.Scope
internal class BluetoothLeScannerImpl @Inject internal constructor(
    private val context: Context
) : BluetoothLeScanner {

    private var lastStartScan = Duration.ZERO

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

        // Anti-spam mechanism to avoid an exception when requesting 6 scans within a 30s frame
        delay(5.seconds - (System.currentTimeMillis().milliseconds - lastStartScan))
        // Delay elapsed, set lastStartScan to the current timestamp
        lastStartScan = System.currentTimeMillis().milliseconds

        val leScanner = bluetoothAdapter!!.bluetoothLeScanner
        leScanner.startScan(
            listOf(
                ScanFilter
                    .Builder()
                    .setServiceUuid(SYSGRATION_SERVICE_UUID)
                    .build(),
                ScanFilter
                    .Builder()
                    .setServiceUuid(PECHAM_SERVICE_UUID)
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
            if (bluetoothAdapter?.isEnabled == true) {
                leScanner.flushPendingScanResults(callback)
                leScanner.stopScan(callback)
            }
        }
    }.flowOn(Dispatchers.Main) // System's BluetoothLeScanner class as issues if called on a background thread
        .mapNotNull { result ->
            result
                .scanRecord
                ?.manufacturerSpecificData
                ?.takeIf { it.size > 0 }
                ?.valueAt(0)
                ?.let { RawPecham(result, it) ?: RawSysgration(it) }
        }
        .mapNotNull { it.asTyre() }

    private val lowLatencyScanFlow = scan(ScanSettings.SCAN_MODE_LOW_LATENCY).shared()

    override fun highDutyScan(): Flow<Tyre> = lowLatencyScanFlow

    @SuppressLint("MissingPermission")
    private val balancedScanFlow = scan(ScanSettings.SCAN_MODE_BALANCED).shared()

    override fun normalScan(): Flow<Tyre> = balancedScanFlow

    @OptIn(DelicateCoroutinesApi::class)
    private fun Flow<Tyre>.shared() = this
        .materializeCompletion()
        .shareIn(GlobalScope + Dispatchers.Default, WhileSubscribed())
        .dematerializeCompletion()

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

    override val isBluetoothRequired = true

    @OptIn(ExperimentalUnsignedTypes::class)
    companion object {
        private val SYSGRATION_SERVICE_UUID = ParcelUuid(
            fromString("0000fbb0-0000-1000-8000-00805f9b34fb")
        )
        private val PECHAM_SERVICE_UUID = ParcelUuid(
            fromString("000027a5-0000-1000-8000-00805f9b34fb")
        )
    }
}

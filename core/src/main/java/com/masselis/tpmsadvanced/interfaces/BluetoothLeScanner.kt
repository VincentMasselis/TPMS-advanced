package com.masselis.tpmsadvanced.interfaces

import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.*
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@SuppressLint("InlinedApi")
class BluetoothLeScanner @Inject constructor(
    private val context: Context
) {
    @SuppressLint("MissingPermission")
    @RequiresPermission(BLUETOOTH_SCAN)
    val scanFlow = callbackFlow {
        if (ActivityCompat.checkSelfPermission(context, BLUETOOTH_SCAN) != PERMISSION_GRANTED)
            close(MissingPermissionException(BLUETOOTH_SCAN))

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
        val leScanner = context
            .getSystemService<BluetoothManager>()!!
            .adapter
            .bluetoothLeScanner
        leScanner.startScan(
            listOf(
                ScanFilter
                    .Builder()
                    .setServiceUuid(ParcelUuid(SYSGRATION_SERVICE_UUID))
                    .build()
            ),
            ScanSettings
                .Builder()
                .setScanMode(SCAN_MODE_LOW_LATENCY)
                .setMatchMode(MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(MATCH_NUM_ONE_ADVERTISEMENT)
                .build(),
            callback
        )
        awaitClose {
            leScanner.flushPendingScanResults(callback)
            leScanner.stopScan(callback)
        }
    }.shareIn(
        CoroutineScope(Dispatchers.Main),
        SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds, 0)
    )

    class MissingPermissionException(val permission: String) : Exception()
    class ScanFailed(val reason: Int) : Exception()

    companion object {
        // TODO Replace randomUUID by the right value
        val SYSGRATION_SERVICE_UUID = UUID.randomUUID()!!
    }
}
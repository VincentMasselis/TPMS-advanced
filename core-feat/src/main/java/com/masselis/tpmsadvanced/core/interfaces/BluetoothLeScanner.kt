package com.masselis.tpmsadvanced.core.interfaces

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
import com.masselis.tpmsadvanced.common.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.UUID.fromString
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BluetoothLeScanner @Inject constructor() {

    @SuppressLint("InlinedApi")
    @RequiresPermission("android.permission.BLUETOOTH_SCAN")
    fun scan(mode: Int) = callbackFlow {
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

    class ScanFailed(val reason: Int) : Exception()

    companion object {
        private val SYSGRATION_SERVICE_UUID =
            ParcelUuid(fromString("0000fbb0-0000-1000-8000-00805f9b34fb"))
    }
}
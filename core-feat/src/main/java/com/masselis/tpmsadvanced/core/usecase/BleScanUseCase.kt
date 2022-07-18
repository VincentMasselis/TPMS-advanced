package com.masselis.tpmsadvanced.core.usecase

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.content.getSystemService
import com.masselis.tpmsadvanced.common.appContext
import com.masselis.tpmsadvanced.common.asFlow
import com.masselis.tpmsadvanced.core.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.core.ioc.CoreSingleton
import com.masselis.tpmsadvanced.core.model.Record.Companion.asRawRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@SuppressLint("MissingPermission")
@CoreSingleton
class BleScanUseCase @Inject constructor(bleScanner: BluetoothLeScanner) {

    private val bluetoothAdapter = appContext.getSystemService<BluetoothManager>()!!.adapter

    @SuppressLint("InlinedApi")
    fun missingPermission(): List<String> = when (Build.VERSION.SDK_INT) {
        in Int.MIN_VALUE..28 -> listOf(ACCESS_COARSE_LOCATION)
        in 29..30 -> listOf(ACCESS_FINE_LOCATION)
        in 31..Int.MAX_VALUE -> listOf(BLUETOOTH_CONNECT, BLUETOOTH_SCAN)
        else -> throw IllegalArgumentException()
    }.filter { checkSelfPermission(appContext, it) != PackageManager.PERMISSION_GRANTED }

    fun isChipTurnedOn(): Flow<Boolean> = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        .asFlow()
        .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) }
        .filter { it != BluetoothAdapter.ERROR }
        .map { it == BluetoothAdapter.STATE_ON }
        .onStart { emit(bluetoothAdapter.isEnabled) }

    private val lowLatencyScanFlow = bleScanner.scan(ScanSettings.SCAN_MODE_LOW_LATENCY).shared()

    fun highDutyScan() = lowLatencyScanFlow

    @SuppressLint("MissingPermission")
    private val balancedScanFlow = bleScanner.scan(ScanSettings.SCAN_MODE_BALANCED).shared()

    fun normalScan() = balancedScanFlow

    private fun Flow<ScanResult>.shared() = shareIn(
        CoroutineScope(EmptyCoroutineContext),
        SharingStarted.WhileSubscribed(5.seconds, Duration.ZERO)
    ).asRawRecord()
}
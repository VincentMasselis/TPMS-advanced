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
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.content.getSystemService
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.common.asFlow
import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

public interface BluetoothLeScanner {
    public class ScanFailed(public val reason: Int) : Exception()

    public fun highDutyScan(): Flow<Tyre>
    public fun normalScan(): Flow<Tyre>

    public companion object {

        private val bluetoothAdapter by lazy { appContext.getSystemService<BluetoothManager>()!!.adapter }

        @SuppressLint("InlinedApi")
        @Suppress("MagicNumber")
        public fun missingPermission(): List<String> = when (Build.VERSION.SDK_INT) {
            in Int.MIN_VALUE..28 -> listOf(ACCESS_COARSE_LOCATION)
            in 29..30 -> listOf(ACCESS_FINE_LOCATION)
            in 31..Int.MAX_VALUE -> listOf(BLUETOOTH_CONNECT, BLUETOOTH_SCAN)
            else ->
                @Suppress("ThrowingExceptionsWithoutMessageOrCause")
                throw IllegalArgumentException()
        }.filter { checkSelfPermission(appContext, it) != PERMISSION_GRANTED }

        public fun isChipTurnedOn(): Flow<Boolean> = IntentFilter(ACTION_STATE_CHANGED)
            .asFlow()
            .map { it.getIntExtra(EXTRA_STATE, ERROR) }
            .filter { it != ERROR }
            .map { it == STATE_ON }
            .onStart { emit(bluetoothAdapter.isEnabled) }
    }
}
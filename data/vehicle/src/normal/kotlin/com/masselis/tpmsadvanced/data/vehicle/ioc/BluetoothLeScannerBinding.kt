package com.masselis.tpmsadvanced.data.vehicle.ioc

import android.content.Context
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.BluetoothLeScannerImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface BluetoothLeScannerBinding {
    @Provides
    @SingleIn(AppScope::class)
    private fun bluetoothLeScannerImpl(context: Context): BluetoothLeScanner =
        BluetoothLeScannerImpl(context)
}
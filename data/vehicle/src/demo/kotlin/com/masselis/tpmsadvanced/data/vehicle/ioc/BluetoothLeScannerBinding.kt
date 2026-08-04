package com.masselis.tpmsadvanced.data.vehicle.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.DemoLeScanner
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface BluetoothLeScannerBinding {
    @SingleIn(AppScope::class)
    @Provides
    private fun bluetoothLeScannerImpl(): BluetoothLeScanner = DemoLeScanner()
}
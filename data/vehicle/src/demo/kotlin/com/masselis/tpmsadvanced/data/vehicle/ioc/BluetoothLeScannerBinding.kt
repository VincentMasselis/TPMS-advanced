package com.masselis.tpmsadvanced.data.vehicle.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.DemoLeScanner
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@BindingContainer
internal object BluetoothLeScannerBinding {
    @SingleIn(AppScope::class)
    @Provides
    private fun bluetoothLeScannerImpl(): BluetoothLeScanner = DemoLeScanner()
}
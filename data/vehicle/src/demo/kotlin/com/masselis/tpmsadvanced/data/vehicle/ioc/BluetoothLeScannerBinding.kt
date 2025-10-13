package com.masselis.tpmsadvanced.data.vehicle.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.BluetoothLeScannerImpl
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@Suppress("unused")
@BindingContainer
internal object BluetoothLeScannerBinding {
    @Provides
    private fun bluetoothLeScannerImpl(): BluetoothLeScanner = BluetoothLeScannerImpl()
}
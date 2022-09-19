package com.masselis.tpmsadvanced.data.record.ioc

import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScannerImpl
import dagger.Binds

@dagger.Module
internal interface Module {
    @Binds
    fun scanner(impl: BluetoothLeScannerImpl): BluetoothLeScanner
}
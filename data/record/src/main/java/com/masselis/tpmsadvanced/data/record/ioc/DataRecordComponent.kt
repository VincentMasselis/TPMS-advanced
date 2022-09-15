package com.masselis.tpmsadvanced.data.record.ioc

import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import dagger.Component

@SingleInstance
@Component
public abstract class DataRecordComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(): DataRecordComponent
    }

    public abstract val bluetoothLeScanner: BluetoothLeScanner
}

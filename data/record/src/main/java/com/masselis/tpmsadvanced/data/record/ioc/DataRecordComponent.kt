package com.masselis.tpmsadvanced.data.record.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import dagger.Component

@SingleInstance
@Component(
    dependencies = [CoreCommonComponent::class],
    modules = [Module::class]
)
public abstract class DataRecordComponent {
    @Component.Factory
    internal abstract class Factory {
        abstract fun build(coreCommonComponent: CoreCommonComponent): DataRecordComponent
    }

    public abstract val bluetoothLeScanner: BluetoothLeScanner
}

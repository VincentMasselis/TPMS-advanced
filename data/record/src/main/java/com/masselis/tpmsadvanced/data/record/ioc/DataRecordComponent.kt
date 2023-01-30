package com.masselis.tpmsadvanced.data.record.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import dagger.Component

@SingleInstance
@Component(
    dependencies = [CoreCommonComponent::class],
    modules = [Module::class]
)
public interface DataRecordComponent {
    @Component.Factory
    public interface Factory {
        public fun build(coreCommonComponent: CoreCommonComponent = CoreCommonComponent): DataRecordComponent
    }

    public val bluetoothLeScanner: BluetoothLeScanner

    public companion object : DataRecordComponent by DaggerDataRecordComponent
        .factory()
        .build()
}

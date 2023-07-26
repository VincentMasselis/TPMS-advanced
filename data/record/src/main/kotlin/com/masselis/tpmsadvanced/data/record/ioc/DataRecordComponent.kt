package com.masselis.tpmsadvanced.data.record.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import dagger.Component

@DataRecordComponent.Scope
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

    @javax.inject.Scope
    public annotation class Scope

    public companion object : DataRecordComponent by DaggerDataRecordComponent
        .factory()
        .build()
}

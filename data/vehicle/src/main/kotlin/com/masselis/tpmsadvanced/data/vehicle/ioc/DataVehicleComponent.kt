@file:Suppress("PropertyName", "VariableNaming")

package com.masselis.tpmsadvanced.data.vehicle.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.common.koinApplicationComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import dagger.Component
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module


public interface DataVehicleComponent {

    public fun vehicleDatabase(): VehicleDatabase
    public fun sensorDatabase(): SensorDatabase
    public fun bluetoothLeScanner(): BluetoothLeScanner
    public fun tyreDatabase(): TyreDatabase

    public companion object : DataVehicleComponent,
        KoinComponent by koinApplicationComponent({
            modules(
                DatabaseModule,
                BluetoothScannerModule.module
            )
        }) {
        override fun vehicleDatabase(): VehicleDatabase = get()
        override fun sensorDatabase(): SensorDatabase = get()
        override fun bluetoothLeScanner(): BluetoothLeScanner = get()
        override fun tyreDatabase(): TyreDatabase = get()
        public val module: Module = module {
            factory { vehicleDatabase() }
            factory { sensorDatabase() }
            factory { bluetoothLeScanner() }
            factory { tyreDatabase() }
        }
    }
}

@DataVehicleComponent.Scope
@Component(
    dependencies = [CoreCommonComponent::class],
    modules = [DatabaseModule::class]
)
internal interface InternalComponent : DataVehicleComponent {

    val DebugComponent: DebugComponent.Factory

    companion object : InternalComponent by DaggerInternalComponent
        .builder()
        .coreCommonComponent(CoreCommonComponent)
        .build()
}

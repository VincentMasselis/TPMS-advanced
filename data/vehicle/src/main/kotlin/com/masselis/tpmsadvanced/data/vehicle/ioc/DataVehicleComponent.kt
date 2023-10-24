package com.masselis.tpmsadvanced.data.vehicle.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import dagger.Component

@DataVehicleComponent.Scope
@Component(
    dependencies = [CoreCommonComponent::class],
    modules = [Module::class]
)
public interface DataVehicleComponent {
    @Component.Factory
    public interface Factory {
        public fun build(coreCommonComponent: CoreCommonComponent = CoreCommonComponent): DataVehicleComponent
    }

    public val debugComponentFactory: DebugComponent.Factory

    public val vehicleDatabase: VehicleDatabase
    public val sensorDatabase: SensorDatabase
    public val bluetoothLeScanner: BluetoothLeScanner
    public val tyreDatabase: TyreDatabase

    @javax.inject.Scope
    public annotation class Scope

    public companion object : DataVehicleComponent by DaggerDataVehicleComponent
        .factory()
        .build()
}

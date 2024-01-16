@file:Suppress("PropertyName", "VariableNaming")

package com.masselis.tpmsadvanced.data.vehicle.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import dagger.Component


public interface DataVehicleComponent {

    public val vehicleDatabase: VehicleDatabase
    public val sensorDatabase: SensorDatabase
    public val bluetoothLeScanner: BluetoothLeScanner
    public val tyreDatabase: TyreDatabase

    @javax.inject.Scope
    public annotation class Scope

    public companion object : DataVehicleComponent by InternalComponent
}

@DataVehicleComponent.Scope
@Component(
    dependencies = [CoreCommonComponent::class],
    modules = [Module::class]
)
internal interface InternalComponent : DataVehicleComponent {

    val DebugComponent: DebugComponent.Factory

    companion object : InternalComponent by DaggerInternalComponent
        .builder()
        .coreCommonComponent(CoreCommonComponent)
        .build()
}

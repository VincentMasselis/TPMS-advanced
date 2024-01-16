package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.Binds
import dagger.Module

@Module(subcomponents = [InternalVehicleComponent::class])
internal interface VehicleComponentModule {
    @Binds
    fun factory(factory: InternalVehicleComponent.Factory): (Vehicle) -> InternalVehicleComponent
}

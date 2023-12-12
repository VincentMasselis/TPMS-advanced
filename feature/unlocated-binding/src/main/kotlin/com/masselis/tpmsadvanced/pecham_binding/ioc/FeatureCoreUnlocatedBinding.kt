package com.masselis.tpmsadvanced.pecham_binding.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.pecham_binding.interfaces.ListSensorViewModelImpl
import dagger.Component

@Suppress("PropertyName")
@FeatureCoreUnlocatedBinding.Scope
@Component(
    dependencies = [
        DataUnitComponent::class,
        DataVehicleComponent::class,
    ]
)
internal interface FeatureCoreUnlocatedBinding {

    @javax.inject.Scope
    annotation class Scope


    val ListSensorViewModel: ListSensorViewModelImpl.Factory

    companion object : FeatureCoreUnlocatedBinding by DaggerFeatureCoreUnlocatedBinding
        .builder()
        .dataVehicleComponent(DataVehicleComponent)
        .build()
}
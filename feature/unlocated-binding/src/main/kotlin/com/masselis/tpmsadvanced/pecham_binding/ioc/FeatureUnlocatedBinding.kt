package com.masselis.tpmsadvanced.pecham_binding.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.BindSensorViewModelImpl
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.ListSensorViewModelImpl
import dagger.Component

@Suppress("PropertyName")
@FeatureUnlocatedBinding.Scope
@Component(
    dependencies = [
        DataUnitComponent::class,
        DataVehicleComponent::class,
    ]
)
internal interface FeatureUnlocatedBinding {

    @javax.inject.Scope
    annotation class Scope


    val ListSensorViewModel: ListSensorViewModelImpl.Factory
    val BindSensorViewModel: BindSensorViewModelImpl.Factory

    companion object : FeatureUnlocatedBinding by DaggerFeatureUnlocatedBinding
        .builder()
        .dataVehicleComponent(DataVehicleComponent)
        .build()
}
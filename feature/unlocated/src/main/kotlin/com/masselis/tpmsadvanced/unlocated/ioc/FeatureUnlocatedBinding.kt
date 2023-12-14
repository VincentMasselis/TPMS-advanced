package com.masselis.tpmsadvanced.unlocated.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.BindSensorViewModelImpl
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.ListSensorViewModelImpl
import dagger.Component

@Suppress("PropertyName", "FunctionName")
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
        .dataUnitComponent(DataUnitComponent)
        .dataVehicleComponent(DataVehicleComponent)
        .build()
}
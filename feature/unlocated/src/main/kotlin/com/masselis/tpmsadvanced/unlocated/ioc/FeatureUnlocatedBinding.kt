package com.masselis.tpmsadvanced.unlocated.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.BindDialogViewModelImpl
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.ListSensorViewModelImpl
import dagger.Component

@Suppress("PropertyName", "VariableNaming")
@FeatureUnlocatedBinding.Scope
@Component(
    dependencies = [
        DataUnitComponent::class,
        DataVehicleComponent::class,
        FeatureCoreComponent::class,
    ]
)
internal interface FeatureUnlocatedBinding {

    @javax.inject.Scope
    annotation class Scope

    val ListSensorViewModel: ListSensorViewModelImpl.Factory
    val BindDialogViewModel: BindDialogViewModelImpl.Factory

    companion object : FeatureUnlocatedBinding by DaggerFeatureUnlocatedBinding
        .builder()
        .dataUnitComponent(DataUnitComponent)
        .dataVehicleComponent(DataVehicleComponent)
        .featureCoreComponent(FeatureCoreComponent)
        .build()
}

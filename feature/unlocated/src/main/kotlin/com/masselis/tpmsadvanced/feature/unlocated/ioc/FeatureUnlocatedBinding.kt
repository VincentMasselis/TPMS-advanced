package com.masselis.tpmsadvanced.feature.unlocated.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitGraph
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainGraph
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.BindDialogViewModelImpl
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.ListSensorViewModelImpl
import dagger.Component

@Suppress("PropertyName", "VariableNaming")
@FeatureUnlocatedBinding.Scope
@Component(
    dependencies = [
        DataUnitGraph::class,
        DataVehicleComponent::class,
        FeatureMainGraph::class,
    ]
)
internal interface FeatureUnlocatedBinding {

    @javax.inject.Scope
    annotation class Scope

    val ListSensorViewModel: ListSensorViewModelImpl.Factory
    val BindDialogViewModel: BindDialogViewModelImpl.Factory

    companion object : FeatureUnlocatedBinding by DaggerFeatureUnlocatedBinding.builder()
        .dataUnitGraph(DataUnitGraph)
        .dataVehicleComponent(DataVehicleComponent)
        .featureCoreComponent(FeatureMainGraph)
        .build()
}

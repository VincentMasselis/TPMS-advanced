package com.masselis.tpmsadvanced

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureCoreComponent
import dagger.Component

@Suppress("PropertyName", "VariableNaming")
@AutomotiveComponent.Scope
@Component(
    dependencies = [
        FeatureCoreComponent::class,
        DataVehicleComponent::class,
        DataUnitComponent::class,
    ]
)
internal interface AutomotiveComponent {
    @javax.inject.Scope
    annotation class Scope

    val HomeViewModelFactory: HomeViewModel.Factory
    val TpmsScreenFactory: TpmsScreen.Factory

    companion object : AutomotiveComponent by DaggerAutomotiveComponent
        .builder()
        .featureCoreComponent(FeatureCoreComponent)
        .dataVehicleComponent(DataVehicleComponent)
        .dataUnitComponent(DataUnitComponent)
        .build()
}
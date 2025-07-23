package com.masselis.tpmsadvanced.interfaces.ioc

import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel
import dagger.Component

@Suppress("PropertyName", "VariableNaming")
@AppPhoneComponent.Scope
@Component(
    dependencies = [
        FeatureMainComponent::class
    ]
)
internal interface AppPhoneComponent {
    @javax.inject.Scope
    annotation class Scope

    val HomeViewModel: HomeViewModel.Factory
    fun VehicleHomeViewModel(): VehicleHomeViewModel

    companion object : AppPhoneComponent by DaggerAppPhoneComponent
        .builder()
        .featureMainComponent(FeatureMainComponent)
        .build()
}

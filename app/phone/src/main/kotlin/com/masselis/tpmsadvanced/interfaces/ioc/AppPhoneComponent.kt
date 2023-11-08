package com.masselis.tpmsadvanced.interfaces.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel
import dagger.Component

@Suppress("PropertyName", "VariableNaming")
@AppPhoneComponent.Scope
@Component(
    dependencies = [
        FeatureCoreComponent::class
    ]
)
internal interface AppPhoneComponent {
    @Component.Factory
    interface Factory {
        fun build(featureCoreComponent: FeatureCoreComponent = FeatureCoreComponent): AppPhoneComponent
    }

    @javax.inject.Scope
    annotation class Scope

    val HomeViewModel: HomeViewModel.Factory
    fun VehicleHomeViewModel(): VehicleHomeViewModel

    companion object : AppPhoneComponent by DaggerAppPhoneComponent
        .factory()
        .build()
}

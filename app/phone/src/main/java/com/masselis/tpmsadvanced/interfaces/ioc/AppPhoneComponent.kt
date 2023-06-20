package com.masselis.tpmsadvanced.interfaces.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import dagger.Component

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

    val homeViewModel: HomeViewModel

    companion object : AppPhoneComponent by DaggerAppPhoneComponent
        .factory()
        .build()
}

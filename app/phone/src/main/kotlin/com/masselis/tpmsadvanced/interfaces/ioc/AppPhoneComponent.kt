package com.masselis.tpmsadvanced.interfaces.ioc

import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

@Suppress("PropertyName", "VariableNaming")
@DependencyGraph(
    AppPhoneComponent::class,
    bindingContainers = [Bindings::class]
)
internal interface AppPhoneComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes featureMainComponent: FeatureMainComponent): AppPhoneComponent
    }

    val HomeViewModel: HomeViewModel.Factory
    fun VehicleHomeViewModel(): VehicleHomeViewModel

    companion object : AppPhoneComponent by createGraphFactory<Factory>().build(
        FeatureMainComponent
    )
}

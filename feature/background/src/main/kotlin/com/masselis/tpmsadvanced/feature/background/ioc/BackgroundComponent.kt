package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

@Suppress("unused")
@DependencyGraph(
    BackgroundComponent::class,
    bindingContainers = [Bindings::class]
)
internal interface BackgroundComponent {

    @DependencyGraph.Factory
    interface Factory : (VehicleComponent) -> BackgroundComponent {
        override operator fun invoke(
            @Includes vehicleComponent: VehicleComponent
        ): BackgroundComponent

        companion object :
                (VehicleComponent) -> BackgroundComponent by createGraphFactory<Factory>()
    }

    fun BackgroundViewModel(): BackgroundViewModel

    companion object : (VehicleComponent) -> BackgroundComponent by Factory

}

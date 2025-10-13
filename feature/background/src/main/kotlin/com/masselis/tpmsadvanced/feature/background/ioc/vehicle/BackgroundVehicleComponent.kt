package com.masselis.tpmsadvanced.feature.background.ioc.vehicle

import android.app.Service
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Suppress("unused")
@DependencyGraph(
    BackgroundVehicleComponent::class,
    bindingContainers = [Bindings::class]
)
internal interface BackgroundVehicleComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Provides foregroundService: Service?,
            @Includes vehicleComponent: VehicleComponent,
            @Provides scope: CoroutineScope,
            @Includes dataUnitComponent: DataUnitComponent,
            @Includes featureBackgroundComponent: FeatureBackgroundComponent,
        ): BackgroundVehicleComponent

        companion object : Factory by createGraphFactory<Factory>() {

            operator fun invoke(
                foregroundService: Service?,
                vehicle: Vehicle
            ) = build(
                foregroundService,
                VehicleComponent.Companion(vehicle),
                CoroutineScope(SupervisorJob()),
                DataUnitComponent.Companion,
                FeatureBackgroundComponent.Companion
            ).apply { serviceNotifier } // Creates an instance of `ServiceNotifier` after build.
        }
    }

    val vehicle: Vehicle

    val scope: CoroutineScope
    val foregroundService: Service?

    val serviceNotifier: ServiceNotifier
}

package com.masselis.tpmsadvanced.feature.background.ioc

import android.app.Service
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitGraph
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleGraph
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named

@DependencyGraph(BackgroundVehicleComponent.Scope::class)
internal interface BackgroundVehicleComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Provides foregroundService: Service?,
            @Provides scope: CoroutineScope,
            @Includes vehicleGraph: VehicleGraph,
            @Includes dataUnitGraph: DataUnitGraph,
            @Includes featureBackgroundComponent: FeatureBackgroundComponent,
        ): BackgroundVehicleComponent

        companion object : Factory by createGraphFactory()
    }

    @javax.inject.Scope
    annotation class Scope

    @get:Named("base")
    val vehicle: Vehicle

    val scope: CoroutineScope
    val foregroundService: Service?

    @Provides
    private fun serviceNotifier(): ServiceNotifier = ServiceNotifier()

    companion object : (Service?, Vehicle) -> BackgroundVehicleComponent {
        override fun invoke(
            foregroundService: Service?,
            vehicle: Vehicle
        ): BackgroundVehicleComponent = DaggerBackgroundVehicleComponent
            .factory()
            .build(
                foregroundService,
                VehicleGraph(vehicle),
                CoroutineScope(SupervisorJob()),
                DataUnitGraph,
                FeatureBackgroundComponent
            )
            .apply { serviceNotifier } // Creates an instance of `ServiceNotifier` after build.
    }
}

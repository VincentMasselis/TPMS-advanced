package com.masselis.tpmsadvanced.feature.background.ioc.vehicle

import android.app.Service
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope

@Suppress("unused")
@DependencyGraph(
    ServiceComponent::class,
    bindingContainers = [Bindings::class]
)
internal interface ServiceComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Provides service: Service,
            @Provides scope: CoroutineScope,
            @Includes vehicleComponent: VehicleComponent,
            @Includes dataUnitComponent: DataUnitComponent,
        ): ServiceComponent

        companion object : (Vehicle, Service, CoroutineScope) -> ServiceComponent {
            private val factory = createGraphFactory<Factory>()
            override operator fun invoke(
                vehicle: Vehicle,
                foregroundService: Service,
                scope: CoroutineScope,
            ) = factory.build(
                foregroundService,
                scope,
                VehicleComponent(vehicle),
                DataUnitComponent,
            ).apply { serviceNotifier } // Creates an instance of `ServiceNotifier` after build.
        }
    }

    val serviceNotifier: ServiceNotifier
}

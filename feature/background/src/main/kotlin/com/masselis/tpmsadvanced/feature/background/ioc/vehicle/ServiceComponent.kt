package com.masselis.tpmsadvanced.feature.background.ioc.vehicle

import android.app.Service
import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope

@Suppress("unused")
@GraphExtension(
    ServiceComponent.Scope::class,
)
public interface ServiceComponent {

    public abstract class Scope private constructor()

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun build(
            @Provides service: Service,
            @Provides scope: CoroutineScope,
            @Includes vehicleComponent: VehicleComponent,
        ): ServiceComponent
    }

    @Provides
    @SingleIn(Scope::class)
    private fun serviceNotifier(
        vehicle: Vehicle,
        vehicleComponent: VehicleComponent,
        scope: CoroutineScope,
        vehicleRangesUseCase: VehicleRangesUseCase,
        unitPreferences: UnitPreferences,
        foregroundService: Service,
    ): ServiceNotifier = ServiceNotifier(
        vehicle,
        vehicleComponent,
        scope,
        vehicleRangesUseCase,
        unitPreferences,
        foregroundService
    )

    public val internal: Internal

    @Inject
    public class Internal internal constructor(
        internal val serviceNotifier: () -> ServiceNotifier
    )

    public companion object {
        public operator fun invoke(
            vehicle: Vehicle,
            foregroundService: Service,
            scope: CoroutineScope,
        ): ServiceComponent = (appGraph as Factory).build(
            foregroundService,
            scope,
            VehicleComponent(vehicle),
        ).apply { ServiceNotifier() } // Creates an instance of `ServiceNotifier` after build.

        @SuppressWarnings("FunctionNaming")
        internal fun ServiceComponent.ServiceNotifier() = internal.serviceNotifier()
    }
}

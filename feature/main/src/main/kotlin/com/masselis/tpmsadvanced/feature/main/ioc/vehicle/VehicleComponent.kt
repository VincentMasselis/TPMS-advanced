package com.masselis.tpmsadvanced.feature.main.ioc.vehicle

import com.masselis.tpmsadvanced.core.ui.Keyed
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.Bindings
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

@Suppress("VariableNaming")
@GraphExtension(
    scope = VehicleComponent.Scope::class,
)
public interface VehicleComponent {

    public abstract class Scope private constructor()

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun build(@Provides vehicle: Vehicle): VehicleComponent

        public companion object {
            public fun VehicleComponent.key(): Keyed =
                mapOf("vehicle_id" to vehicle.uuid.toString())
        }
    }

    public val vehicle: Vehicle
    @VehicleLifecycle
    public val scope: CoroutineScope
    public val vehicleStateFlow: StateFlow<Vehicle>
    public val vehicleRangesUseCase: VehicleRangesUseCase

    public companion object {
        public operator fun invoke(vehicle: Vehicle): VehicleComponent =
            Bindings.vehicleComponentCache.get(vehicle)
    }
}

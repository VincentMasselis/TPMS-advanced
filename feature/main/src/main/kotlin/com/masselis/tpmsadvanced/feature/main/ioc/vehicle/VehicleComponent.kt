package com.masselis.tpmsadvanced.feature.main.ioc.vehicle

import com.masselis.tpmsadvanced.core.ui.KeyHolder
import com.masselis.tpmsadvanced.core.ui.Keyed
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.Bindings
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

@Suppress("VariableNaming")
@GraphExtension(
    scope = VehicleComponent.Scope::class,
)
public interface VehicleComponent : KeyHolder {

    public abstract class Scope private constructor()

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun build(@Provides vehicle: Vehicle): VehicleComponent
    }

    public val vehicle: Vehicle

    @VehicleLifecycle
    public val scope: CoroutineScope
    public val vehicleStateFlow: StateFlow<Vehicle>
    public val vehicleRangesUseCase: VehicleRangesUseCase

    /** Builds the view models of this graph and of [AppScope], see `LocalMetroViewModelFactory` */
    @ForScope(Scope::class)
    public val viewModelFactory: MetroViewModelFactory

    override val key: Keyed
        get() = mapOf("vehicle_id" to vehicle.uuid.toString())

    public companion object {
        public operator fun invoke(vehicle: Vehicle): VehicleComponent =
            Bindings.vehicleComponentCache.get(vehicle)
    }
}

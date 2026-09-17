package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

@Suppress("unused", "FunctionNaming")
@GraphExtension(
    BackgroundComponent.Scope::class,
)
public interface BackgroundComponent {

    public abstract class Scope private constructor()

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun build(@Includes vehicleComponent: VehicleComponent): BackgroundComponent
    }

    @Provides
    private fun backgroundViewModel(vehicle: Vehicle): BackgroundViewModel =
        BackgroundViewModel(vehicle)

    public val internal: Internal

    @Inject
    public class Internal internal constructor(
        internal val backgroundViewModel: () -> BackgroundViewModel
    )

    public companion object {
        public operator fun invoke(vehicle: VehicleComponent): BackgroundComponent =
            (appGraph as Factory).build(vehicle)

        internal fun BackgroundComponent.BackgroundViewModel() = internal.backgroundViewModel()
    }
}

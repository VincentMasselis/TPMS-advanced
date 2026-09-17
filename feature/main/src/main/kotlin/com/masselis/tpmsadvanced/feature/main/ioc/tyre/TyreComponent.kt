package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.core.ui.Keyed
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreSubcomponentBindings.Companion.findTyreComponentUseCase
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides


@Suppress("VariableNaming", "unused")
@GraphExtension(
    scope = TyreComponent.Scope::class,
)
public interface TyreComponent {

    public class Scope private constructor()

    @ContributesTo(VehicleComponent.Scope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun build(@Provides location: Location): TyreComponent
    }

    public val vehicle: Vehicle
    public val location: Location
    public val tyreAtmosphereUseCase: TyreAtmosphereUseCase
    public val tyreIconStateFlow: TyreIconStateFlow
    public val tyreStatsStateFlow: TyreStatsStateFlow

    public companion object {
        public fun TyreComponent.keyed(): Keyed = mapOf(
            "vehicle_id" to vehicle.uuid.toString(),
            "location" to "$location"
        )

        public fun VehicleComponent.TyreComponent(loc: Location): TyreComponent =
            findTyreComponentUseCase(loc)
    }
}

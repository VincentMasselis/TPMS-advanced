package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.core.ui.Keyed
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.BindSensorButtonViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreIconViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreStatsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides


@Suppress("PropertyName", "VariableNaming")
public sealed interface TyreComponent {
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
    }
}

@Suppress("PropertyName", "VariableNaming", "unused", "FunctionName")
@GraphExtension(
    TyreComponent::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalTyreComponent : TyreComponent {

    @GraphExtension.Factory
    interface Factory {
        fun build(@Provides location: Location): InternalTyreComponent
    }

    fun TyreIconViewModel(): TyreIconViewModelImpl
    fun TyreStatsViewModel(): TyreStatsViewModelImpl
    val BindSensorButtonViewModel: BindSensorButtonViewModelImpl.Factory
}

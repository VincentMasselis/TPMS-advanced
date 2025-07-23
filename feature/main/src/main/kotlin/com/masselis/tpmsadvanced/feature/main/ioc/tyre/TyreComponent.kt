package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.BindSensorButtonViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreStatsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.TyreAtmosphereUseCase
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides


public sealed interface TyreComponent {
    @javax.inject.Scope
    public annotation class Scope

    public val vehicle: Vehicle
    public val location: Location
    public val tyreAtmosphereUseCase: TyreAtmosphereUseCase
}

@Suppress("PropertyName", "VariableNaming", "unused")
@GraphExtension(
    TyreComponent::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalTyreComponent : TyreComponent {

    @GraphExtension.Factory
    interface Factory {
        fun build(@Provides location: Location): InternalTyreComponent
    }

    val TyreViewModel: TyreViewModelImpl.Factory
    val TyreStatViewModel: TyreStatsViewModelImpl.Factory
    val BindSensorButtonViewModel: BindSensorButtonViewModelImpl.Factory

    companion object {
        @Composable
        inline fun <reified VM : ViewModel> InternalTyreComponent.viewModel(
            noinline initializer: CreationExtras.(InternalTyreComponent) -> VM
        ) = viewModel(
            keyed = mapOf(
                "vehicle_id" to vehicle.uuid.toString(),
                "location" to "$location"
            ),
            initializer = initializer
        )
    }
}

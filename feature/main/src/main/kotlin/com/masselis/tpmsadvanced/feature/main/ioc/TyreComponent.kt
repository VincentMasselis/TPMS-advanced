package com.masselis.tpmsadvanced.feature.main.ioc

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
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named


public sealed interface TyreComponent {
    @javax.inject.Scope
    public annotation class Scope

    public val location: Location
    public val tyreAtmosphereUseCase: TyreAtmosphereUseCase
}

@Suppress("PropertyName", "VariableNaming")
@TyreComponent.Scope
@Subcomponent(
    modules = [TyreModule::class]
)
internal interface InternalTyreComponent : TyreComponent {
    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance location: Location): InternalTyreComponent
    }

    @get:Named("base")
    val vehicle: Vehicle

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

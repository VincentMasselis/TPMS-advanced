package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.feature.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import dagger.BindsInstance
import dagger.Subcomponent

@TyreComponent.Scope
@Subcomponent(
    modules = [TyreModule::class]
)
public abstract class TyreComponent {

    @Subcomponent.Factory
    internal interface Factory {
        fun build(@BindsInstance locations: Set<SensorLocation>): TyreComponent
    }

    @javax.inject.Scope
    internal annotation class Scope

    public abstract val tyreAtmosphereUseCase: TyreAtmosphereUseCase

    internal abstract val tyreViewModelFactory: TyreViewModelImpl.Factory
    internal abstract val tyreStatViewModelFactory: TyreStatsViewModel.Factory
    internal abstract val bindSensorButtonViewModelFactory: BindSensorButtonViewModel.Factory
}

package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.BindSensorButtonViewModelImpl
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.TyreStatsViewModelImpl
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.feature.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import dagger.BindsInstance
import dagger.Subcomponent


public interface TyreComponent {
    @javax.inject.Scope
    public annotation class Scope

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

    val TyreViewModel: TyreViewModelImpl.Factory
    val TyreStatViewModel: TyreStatsViewModelImpl.Factory
    val BindSensorButtonViewModel: BindSensorButtonViewModelImpl.Factory
}

package com.masselis.tpmsadvanced.core.ioc

import com.masselis.tpmsadvanced.core.interfaces.viewmodel.SensorFavouriteViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.model.TyreLocation
import com.masselis.tpmsadvanced.core.usecase.FavouriteSensorUseCase
import dagger.BindsInstance
import dagger.Subcomponent

@SingleInstance
@Subcomponent(
    modules = [
        TyreComponentModule::class,
    ]
)
public abstract class TyreComponent {

    @Subcomponent.Factory
    internal abstract class Factory {
        abstract fun build(@BindsInstance location: TyreLocation): TyreComponent
    }

    public abstract val favouriteSensorUseCase: FavouriteSensorUseCase

    internal abstract val tyreViewModelFactory: TyreViewModelImpl.Factory
    internal abstract val tyreStatViewModelFactory: TyreStatsViewModel.Factory
    internal abstract val sensorFavouriteViewModelFactory: SensorFavouriteViewModel.Factory
}
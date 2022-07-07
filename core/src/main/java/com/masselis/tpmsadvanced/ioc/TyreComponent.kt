package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.interfaces.viewmodel.SensorFavouriteViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.usecase.FavouriteSensorUseCase
import dagger.BindsInstance
import dagger.Subcomponent

@SingleInstance
@Subcomponent(
    modules = [
        TyreComponentModule::class,
    ]
)
interface TyreComponent {

    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance location: TyreLocation): TyreComponent
    }

    val favouriteSensorUseCase: FavouriteSensorUseCase

    val tyreViewModelFactory: TyreViewModelImpl.Factory
    val tyreStatViewModelFactory: TyreStatsViewModel.Factory
    val sensorFavouriteViewModelFactory: SensorFavouriteViewModel.Factory
}
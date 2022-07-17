package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.common.FirebaseModule
import com.masselis.tpmsadvanced.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.UnitsViewModel
import com.masselis.tpmsadvanced.usecase.FindTyreComponentUseCase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        FirebaseModule::class,
        TyreComponentFactoryModule::class
    ]
)
interface CoreComponent {

    @Component.Factory
    interface Factory {
        fun build(): CoreComponent
    }

    val preconditionsViewModel: PreconditionsViewModel.Factory
    val settingsViewModel: SettingsViewModel.Factory
    val clearFavouritesViewModel: ClearFavouritesViewModel.Factory
    val unitsViewModel: UnitsViewModel

    val findTyreComponentUseCase: FindTyreComponentUseCase
}
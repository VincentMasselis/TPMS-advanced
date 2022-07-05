package com.masselis.tpmsadvanced.ioc

import android.content.Context
import com.masselis.tpmsadvanced.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.UnitsViewModel
import com.masselis.tpmsadvanced.usecase.FindTyreComponentUseCase
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TyreComponentFactoryModule::class
    ]
)
interface MainComponent {

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): MainComponent
    }

    val preconditionsViewModel: PreconditionsViewModel.Factory
    val settingsViewModel: SettingsViewModel.Factory
    val clearFavouritesViewModel: ClearFavouritesViewModel.Factory
    val unitsViewModel: UnitsViewModel

    val findTyreComponentUseCase: FindTyreComponentUseCase
}
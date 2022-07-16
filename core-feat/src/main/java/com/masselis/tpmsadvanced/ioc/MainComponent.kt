package com.masselis.tpmsadvanced.ioc

import android.content.Context
import com.masselis.tpmsadvanced.interfaces.viewmodel.*
import com.masselis.tpmsadvanced.usecase.FindTyreComponentUseCase
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainComponentModule::class,
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
package com.masselis.tpmsadvanced.core.ioc

import com.masselis.tpmsadvanced.common.FirebaseModule
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.UnitsViewModel
import com.masselis.tpmsadvanced.core.usecase.FindTyreComponentUseCase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        FirebaseModule::class,
        TyreComponentFactoryModule::class
    ]
)
public abstract class CoreComponent {

    @Component.Factory
    internal abstract class Factory {
        abstract fun build(): CoreComponent
    }

    public abstract val findTyreComponentUseCase: FindTyreComponentUseCase

    abstract val preconditionsViewModel: PreconditionsViewModel.Factory
    internal abstract val settingsViewModel: SettingsViewModel.Factory
    internal abstract val clearFavouritesViewModel: ClearFavouritesViewModel.Factory
    internal abstract val unitsViewModel: UnitsViewModel
}
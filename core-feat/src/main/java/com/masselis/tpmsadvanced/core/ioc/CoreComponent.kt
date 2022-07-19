package com.masselis.tpmsadvanced.core.ioc

import com.masselis.tpmsadvanced.common.FirebaseModule
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.unit.interfaces.UnitsViewModel
import com.masselis.tpmsadvanced.core.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.unit.ioc.UnitComponent
import dagger.Component

@CoreSingleton
@Component(
    modules = [
        FirebaseModule::class,
        TyreComponentFactoryModule::class
    ],
    dependencies = [
        UnitComponent::class
    ]
)
public abstract class CoreComponent {

    @Component.Factory
    internal abstract class Factory {
        abstract fun build(unitComponent: UnitComponent): CoreComponent
    }

    internal abstract val findTyreComponentUseCase: FindTyreComponentUseCase

    abstract val preconditionsViewModel: PreconditionsViewModel.Factory
    internal abstract val settingsViewModel: SettingsViewModel.Factory
    internal abstract val clearFavouritesViewModel: ClearFavouritesViewModel.Factory
    internal abstract val unitsViewModel: UnitsViewModel
}
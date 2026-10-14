package com.masselis.tpmsadvanced.ioc

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@Suppress("unused")
@BindingContainer
@ContributesTo(AppScope::class)
public object Bindings {

    @Provides
    @IntoMap
    @ViewModelKey(VehicleHomeViewModel::class)
    private fun vehicleHomeViewModel(noveltyUseCase: NoveltyUseCase): ViewModel =
        VehicleHomeViewModel(noveltyUseCase)

    @Provides
    @IntoMap
    @ManualViewModelAssistedFactoryKey(HomeViewModel.Factory::class)
    private fun homeViewModelFactory(factory: HomeViewModel.Factory): ManualViewModelAssistedFactory =
        factory
}

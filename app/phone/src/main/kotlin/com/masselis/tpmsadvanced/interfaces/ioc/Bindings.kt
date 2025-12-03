package com.masselis.tpmsadvanced.interfaces.ioc

import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @Provides
    private fun vehicleHomeViewModel(noveltyUseCase: NoveltyUseCase): VehicleHomeViewModel =
        VehicleHomeViewModel(noveltyUseCase)
}

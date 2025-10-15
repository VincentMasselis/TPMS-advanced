package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @Provides
    private fun backgroundViewModel(vehicle: Vehicle): BackgroundViewModel =
        BackgroundViewModel(vehicle)
}

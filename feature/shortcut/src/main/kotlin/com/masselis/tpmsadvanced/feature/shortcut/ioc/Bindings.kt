package com.masselis.tpmsadvanced.feature.shortcut.ioc

import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.feature.shortcut.usecase.ShortcutUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @Provides
    @SingleIn(AppScope::class)
    private fun shortcutUseCase(
        vehicleListUseCase: VehicleListUseCase
    ): ShortcutUseCase = ShortcutUseCase(vehicleListUseCase)
}

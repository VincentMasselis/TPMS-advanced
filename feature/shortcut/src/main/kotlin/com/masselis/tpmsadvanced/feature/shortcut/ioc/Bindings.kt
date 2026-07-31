package com.masselis.tpmsadvanced.feature.shortcut.ioc

import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.feature.shortcut.usecase.ShortcutUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface Bindings {
    @Provides
    @SingleIn(AppScope::class)
    private fun shortcutUseCase(
        vehicleListUseCase: VehicleListUseCase
    ): ShortcutUseCase = ShortcutUseCase(vehicleListUseCase)

    public val featureShortcutInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val shortcutUseCase: () -> ShortcutUseCase
    )

    @Suppress("FunctionName")
    public companion object : Bindings by appGraph as Bindings {
        internal fun ShortcutUseCase() = featureShortcutInternal.shortcutUseCase
    }
}

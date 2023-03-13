package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent

internal val LocalCarComponent: ProvidableCompositionLocal<CarComponent> =
    compositionLocalOf { error("Not available") }

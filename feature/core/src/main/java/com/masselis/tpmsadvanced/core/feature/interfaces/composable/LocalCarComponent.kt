package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent

internal val LocalVehicleComponent: ProvidableCompositionLocal<VehicleComponent> =
    compositionLocalOf { error("Not available") }

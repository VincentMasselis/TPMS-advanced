package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent

@Suppress("CompositionLocalAllowlist")
public val LocalVehicleComponent: ProvidableCompositionLocal<VehicleComponent> =
    compositionLocalOf { error("Not available") }

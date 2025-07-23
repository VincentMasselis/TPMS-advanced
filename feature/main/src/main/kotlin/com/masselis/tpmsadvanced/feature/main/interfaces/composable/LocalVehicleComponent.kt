package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleGraph

@Suppress("CompositionLocalAllowlist")
public val LocalVehicleGraph: ProvidableCompositionLocal<VehicleGraph> =
    compositionLocalOf { error("Not available") }

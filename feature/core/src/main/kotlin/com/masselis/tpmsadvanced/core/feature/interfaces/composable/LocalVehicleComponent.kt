package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import android.annotation.SuppressLint
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent

@Suppress("CompositionLocalAllowlist")
public val LocalVehicleComponent: ProvidableCompositionLocal<VehicleComponent> =
    compositionLocalOf { error("Not available") }

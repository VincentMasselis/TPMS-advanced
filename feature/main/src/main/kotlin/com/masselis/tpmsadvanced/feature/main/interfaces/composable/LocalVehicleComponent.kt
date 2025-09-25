package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import com.masselis.tpmsadvanced.feature.main.ioc.InternalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent

@Suppress("CompositionLocalAllowlist")
public val LocalVehicleComponent: ProvidableCompositionLocal<VehicleComponent> =
    compositionLocalOf { error("Not available") }

internal val LocalInternalVehicleComponent: ProvidableCompositionLocal<InternalVehicleComponent> =
    compositionLocalWithComputedDefaultOf { LocalVehicleComponent.currentValue as InternalVehicleComponent }

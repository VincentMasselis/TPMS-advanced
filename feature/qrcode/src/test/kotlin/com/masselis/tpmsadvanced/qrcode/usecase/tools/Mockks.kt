package com.masselis.tpmsadvanced.qrcode.usecase.tools

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KVisibility
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.jvm.isAccessible

internal fun mockkCurrentVehicleUseCase(vehicleComponent: VehicleComponent) =
    CurrentVehicleUseCase::class
        .constructors
        .single { it.visibility == PRIVATE }
        .also { it.isAccessible = true }
        .call(
            mockk<VehicleDatabase>(),
            MutableStateFlow(vehicleComponent)
        )
        .let(::spyk)

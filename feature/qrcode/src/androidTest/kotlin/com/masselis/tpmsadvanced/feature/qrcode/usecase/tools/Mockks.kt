package com.masselis.tpmsadvanced.feature.qrcode.usecase.tools

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KVisibility.INTERNAL
import kotlin.reflect.jvm.isAccessible

internal fun mockkCurrentVehicleUseCase(vehicleComponent: VehicleComponent) =
    CurrentVehicleUseCase::class
        .constructors
        .single { it.visibility == INTERNAL }
        .also { it.isAccessible = true }
        .call(
            mockk<VehicleDatabase>(),
            MutableStateFlow(vehicleComponent)
        )
        .let(::spyk)

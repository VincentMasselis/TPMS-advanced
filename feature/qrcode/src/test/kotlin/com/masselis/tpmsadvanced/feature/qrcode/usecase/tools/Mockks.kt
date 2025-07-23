package com.masselis.tpmsadvanced.feature.qrcode.usecase.tools

import com.masselis.tpmsadvanced.feature.main.ioc.VehicleGraph
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.jvm.isAccessible

internal fun mockkCurrentVehicleUseCase(vehicleGraph: VehicleGraph) =
    CurrentVehicleUseCase::class
        .constructors
        .single { it.visibility == PRIVATE }
        .also { it.isAccessible = true }
        .call(
            mockk<VehicleDatabase>(),
            MutableStateFlow(vehicleGraph)
        )
        .let(::spyk)

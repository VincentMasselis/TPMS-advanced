package com.masselis.tpmsadvanced.feature.background.usecase

import com.masselis.tpmsadvanced.core.database.QueryOne
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

internal inline fun <reified T : Any> mockQueryOne(value: T) = mockk<QueryOne<T>> {
    every { execute() } returns value
    every { asFlow(any()) } returns flowOf(value)
    every { asChillFlow(any()) } returns flowOf(value)
}

internal fun mockVehicle(
    uuid: UUID = UUID.randomUUID(),
    kind: Vehicle.Kind = Vehicle.Kind.CAR,
    name: String = "MOCK",
    lowPressure: Pressure = 1f.bar,
    highPressure: Pressure = 5f.bar,
    lowTemp: Temperature = 15f.celsius,
    normalTemp: Temperature = 25f.celsius,
    highTemp: Temperature = 45f.celsius,
    isBackgroundMonitor: Boolean = false,
) = Vehicle(
    uuid, kind, name, lowPressure, highPressure, lowTemp, normalTemp, highTemp, isBackgroundMonitor
)
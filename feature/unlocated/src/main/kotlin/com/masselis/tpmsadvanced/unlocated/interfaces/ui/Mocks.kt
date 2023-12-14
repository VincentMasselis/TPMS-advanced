package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import java.util.UUID


internal fun mockTyre(
    id: Int,
    timestamp: Double = now(),
    rssi: Int = -20,
    pressure: Pressure = 2f.bar,
    temperature: Temperature = 20f.celsius,
    battery: UShort = 50u,
    isAlarm: Boolean = false
) = Tyre.Unlocated(
    timestamp,
    rssi,
    id,
    pressure,
    temperature,
    battery,
    isAlarm
)

internal fun mockSensor(
    id: Int, location: Location = Location.Wheel(FRONT_LEFT)
) = Sensor(id, location)

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

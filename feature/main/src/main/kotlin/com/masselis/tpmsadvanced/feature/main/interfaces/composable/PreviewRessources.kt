package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import java.util.UUID

internal val previewVehicle = Vehicle(
    UUID.randomUUID(),
    Vehicle.Kind.CAR,
    "PREVIEW",
    0.8f.bar,
    2.5f.bar,
    false,
    0.8f.bar,
    2.5f.bar,
    5f.celsius,
    40f.celsius,
    90f.celsius,
)

internal val previewSensor = Sensor(
    1,
    Location.Wheel.FRONT_LEFT
)

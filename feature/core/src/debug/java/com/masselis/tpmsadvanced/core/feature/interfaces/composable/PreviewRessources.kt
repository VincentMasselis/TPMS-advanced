package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import java.util.UUID

internal val previewVehicle = Vehicle(
    UUID.randomUUID(),
    Vehicle.Kind.CAR,
    "PREVIEW",
    0.8f.bar,
    2.5f.bar,
    5f.celsius,
    40f.celsius,
    90f.celsius,
    false
)

internal val previewSensor = Sensor(
    1,
    SensorLocation.REAR_LEFT
)

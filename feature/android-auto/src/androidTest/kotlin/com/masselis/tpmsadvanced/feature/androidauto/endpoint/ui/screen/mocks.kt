package com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.screen

import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import java.util.UUID


internal fun mockVehicle() = Vehicle(
    UUID.randomUUID(),
    Vehicle.Kind.CAR,
    "MOCK",
    1f.bar,
    5f.bar,
    15f.celsius,
    25f.celsius,
    45f.celsius,
)
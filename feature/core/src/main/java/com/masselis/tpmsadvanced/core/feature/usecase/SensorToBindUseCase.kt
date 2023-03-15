package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Sensor
import java.util.*
import javax.inject.Inject

internal class SensorToBindUseCase @Inject constructor(
    private val carId: UUID,
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
) {
    fun boundVehicle(sensor: Sensor) = vehicleDatabase.selectBySensorId(sensor.id)
    suspend fun upsert(sensor: Sensor) = sensorDatabase.upsert(sensor, carId)
}

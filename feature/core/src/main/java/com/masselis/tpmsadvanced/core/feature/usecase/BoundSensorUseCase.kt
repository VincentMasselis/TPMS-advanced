package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.model.Sensor
import java.util.*
import javax.inject.Inject

internal class BoundSensorUseCase @Inject constructor(
    private val carId: UUID,
    private val carDatabase: CarDatabase,
    private val sensorDatabase: SensorDatabase,
) {
    fun boundCar(sensorId: Int) = carDatabase.selectBySensorId(sensorId)
    suspend fun upsert(sensor: Sensor) = sensorDatabase.upsert(sensor, carId)
}

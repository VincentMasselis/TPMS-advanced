package com.masselis.tpmsadvanced.qrcode.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.qrcode.model.SensorMap
import javax.inject.Inject

internal class BoundSensorMapUseCase @Inject constructor(
    private val sensorDatabase: SensorDatabase,
    private val carDatabase: CarDatabase,
) {
    suspend fun bind(ids: SensorMap) {
        val currentCar = carDatabase.currentCar()
        sensorDatabase.deleteFromCar(currentCar.uuid)
        ids.values.forEach { sensor ->
            sensorDatabase.upsert(sensor, currentCar.uuid)
        }
    }
}

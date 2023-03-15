package com.masselis.tpmsadvanced.qrcode.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.qrcode.model.SensorMap
import javax.inject.Inject

internal class BoundSensorMapUseCase @Inject constructor(
    private val sensorDatabase: SensorDatabase,
    private val vehicleDatabase: VehicleDatabase,
) {
    suspend fun bind(ids: SensorMap) {
        val currentVehicle = vehicleDatabase.currentVehicle()
        ids.values.forEach { sensor ->
            sensorDatabase.upsert(sensor, currentVehicle.uuid)
        }
    }
}

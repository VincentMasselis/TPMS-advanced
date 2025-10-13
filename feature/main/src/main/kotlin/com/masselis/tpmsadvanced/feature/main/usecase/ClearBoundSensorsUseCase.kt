package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class ClearBoundSensorsUseCase(
    private val vehicle: Vehicle,
    private val sensorDatabase: SensorDatabase,
) {

    suspend fun clear() = sensorDatabase.deleteFromVehicle(vehicle.uuid)

    fun isClearingAllowed() = sensorDatabase.countByVehicle(vehicle.uuid)
        .asFlow()
        .map { it > 0 }
        .distinctUntilChanged()

}

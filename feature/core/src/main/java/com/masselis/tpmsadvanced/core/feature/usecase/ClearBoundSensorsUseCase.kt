package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

internal class ClearBoundSensorsUseCase @Inject constructor(
    private val vehicleId: UUID,
    private val sensorDatabase: SensorDatabase,
) {

    suspend fun clear() = sensorDatabase.deleteFromVehicle(vehicleId)

    fun isClearingAllowed() = sensorDatabase.countByVehicle(vehicleId)
        .map { it > 0 }
        .distinctUntilChanged()

}

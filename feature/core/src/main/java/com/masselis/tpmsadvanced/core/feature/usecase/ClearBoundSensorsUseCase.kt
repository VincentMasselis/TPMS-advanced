package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

internal class ClearBoundSensorsUseCase @Inject constructor(
    private val carId: UUID,
    private val sensorDatabase: SensorDatabase,
) {

    suspend fun clear() = sensorDatabase.deleteFromCar(carId)

    fun isClearingAllowed() = sensorDatabase.countByCar(carId)
        .map { it > 0 }
        .distinctUntilChanged()

}

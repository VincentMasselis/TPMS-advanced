package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*
import javax.inject.Inject

internal class BoundSensorUseCase @Inject constructor(
    private val carId: UUID,
    private val location: SensorLocation,
    private val sensorDatabase: SensorDatabase,
) {
    fun boundSensor() = sensorDatabase
        .selectByCarAndLocationFlow(carId, location)
        .distinctUntilChanged()
}

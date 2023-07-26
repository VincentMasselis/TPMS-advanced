package com.masselis.tpmsadvanced.qrcode.usecase

import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.qrcode.model.SensorMap
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class BoundSensorMapUseCase @Inject constructor(
    private val sensorDatabase: SensorDatabase,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
) {
    suspend fun bind(ids: SensorMap) = withContext(IO) {
        val currentUuid = currentVehicleUseCase.value.vehicle.uuid
        ids.values
            .map { sensor -> async { sensorDatabase.upsert(sensor, currentUuid) } }
            .awaitAll()
    }
}

package com.masselis.tpmsadvanced.usecase

import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorBatteryUseCase @Inject constructor(
    private val sensorBytesUseCase: SensorBytesUseCase
) {
    fun listen() = sensorBytesUseCase.listen().map { it[16].toInt() }
}
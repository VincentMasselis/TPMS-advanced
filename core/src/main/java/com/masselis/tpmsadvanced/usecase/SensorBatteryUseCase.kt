package com.masselis.tpmsadvanced.usecase

import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorBatteryUseCase @Inject constructor(
    private val sensorByteArrayUseCase: SensorByteArrayUseCase
) {
    fun listen() = sensorByteArrayUseCase.listen().map { it[14].toInt() }
}
package com.masselis.tpmsadvanced.usecase

import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorBatteryUseCase @Inject constructor(
    private val sensorByteArrayUseCaseImpl: SensorByteArrayUseCase
) {
    fun listen() = sensorByteArrayUseCaseImpl.listen().map { it[14].toInt() }
}
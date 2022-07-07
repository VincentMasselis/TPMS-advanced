package com.masselis.tpmsadvanced.usecase

import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorBatteryUseCase @Inject constructor(
    private val recordUseCaseImpl: RecordUseCase
) {
    fun listen() = recordUseCaseImpl.listen().map { it.battery().toInt() }
}
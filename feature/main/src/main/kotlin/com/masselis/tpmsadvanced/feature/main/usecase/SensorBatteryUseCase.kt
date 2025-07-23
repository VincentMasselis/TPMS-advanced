package com.masselis.tpmsadvanced.feature.main.usecase

import kotlinx.coroutines.flow.map

internal class SensorBatteryUseCase(
    private val recordUseCaseImpl: ListenTyreUseCase
) {
    fun listen() = recordUseCaseImpl.listen().map { it.battery }
}

package com.masselis.tpmsadvanced.feature.main.usecase

import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SensorBatteryUseCase @Inject constructor(
    private val recordUseCaseImpl: ListenTyreUseCase
) {
    fun listen() = recordUseCaseImpl.listen().map { it.battery }
}

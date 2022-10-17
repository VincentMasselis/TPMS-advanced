package com.masselis.tpmsadvanced.core.feature.usecase

import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SensorBatteryUseCase @Inject constructor(
    private val recordUseCaseImpl: TyreUseCase
) {
    fun listen() = recordUseCaseImpl.listen().map { it.battery }
}

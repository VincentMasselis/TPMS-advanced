package com.masselis.tpmsadvanced.core.feature.usecase

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class BoundSensorTyreUseCase @Inject constructor(
    private val tyreUseCaseImpl: TyreUseCaseImpl,
    private val sensorBindingUseCase: SensorBindingUseCase,
) : TyreUseCase {

    override fun listen() = tyreUseCaseImpl
        .listen()
        .filter {
            val favId = sensorBindingUseCase.boundSensor().first()?.id ?: return@filter true
            favId == it.id
        }

}

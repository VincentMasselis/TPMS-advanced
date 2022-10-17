package com.masselis.tpmsadvanced.core.feature.usecase

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class BoundSensorTyreUseCase @Inject constructor(
    private val tyreUseCaseImpl: TyreUseCaseImpl,
    private val boundSensorUseCase: BoundSensorUseCase,
) : TyreUseCase {

    override fun listen() = tyreUseCaseImpl
        .listen()
        .filter {
            val favId = boundSensorUseCase.boundSensor().first()?.id ?: return@filter true
            favId == it.id
        }

}

package com.masselis.tpmsadvanced.core.feature.usecase

import kotlinx.coroutines.flow.filter

internal class ListenBoundTyreUseCase(
    private val listenTyreUseCase: ListenTyreUseCase,
    private val sensorBindingUseCase: SensorBindingUseCase,
) : ListenTyreUseCase {

    override fun listen() = listenTyreUseCase
        .listen()
        .filter {
            val favId = sensorBindingUseCase.boundSensor()?.id ?: return@filter true
            favId == it.id
        }
}

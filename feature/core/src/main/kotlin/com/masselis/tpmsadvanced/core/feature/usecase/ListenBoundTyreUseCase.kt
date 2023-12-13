package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

internal class ListenBoundTyreUseCase(
    private val listenTyreUseCase: ListenTyreUseCase,
    private val sensorBindingUseCase: SensorBindingUseCase,
) : ListenTyreUseCase {

    override fun listen(): Flow<Tyre.Located> = listenTyreUseCase
        .listen()
        .filter {
            val favId = sensorBindingUseCase.boundSensor()?.id ?: return@filter true
            favId == it.id
        }
}

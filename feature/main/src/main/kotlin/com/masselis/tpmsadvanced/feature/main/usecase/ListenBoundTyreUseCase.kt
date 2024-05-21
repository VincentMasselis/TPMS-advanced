package com.masselis.tpmsadvanced.feature.main.usecase

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
            val boundSensorId = sensorBindingUseCase.boundSensor().value?.id ?: return@filter true
            boundSensorId == it.sensorId
        }
}

package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SearchSensorUseCase @Inject constructor(
    private val location: SensorLocation,
    private val tyreUseCaseImpl: TyreUseCaseImpl,
) {
    fun search() = tyreUseCaseImpl
        .listen()
        .map { Sensor(it.id, location) }
        .distinctUntilChanged()
}

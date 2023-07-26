package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SearchSensorUseCase @Inject constructor(
    private val tyreUseCaseImpl: TyreUseCaseImpl,
) {
    fun search() = tyreUseCaseImpl
        .listen()
        .map { Sensor(it.id, it.location) }
        .distinctUntilChanged()
}

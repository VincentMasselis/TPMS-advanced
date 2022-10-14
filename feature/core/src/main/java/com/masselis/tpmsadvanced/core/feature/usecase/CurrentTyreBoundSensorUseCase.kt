package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreScope
import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.*
import javax.inject.Inject

@TyreScope
internal class CurrentTyreBoundSensorUseCase @Inject constructor(
    private val carId: UUID,
    private val location: SensorLocation,
    private val tyreUseCaseImpl: TyreUseCaseImpl,
    private val tyreDatabase: TyreDatabase,
    sensorDatabase: SensorDatabase,
) : TyreUseCase {

    val foundSensor = tyreUseCaseImpl
        .listen()
        .map { Sensor(it.id, location) }
        .distinctUntilChanged()

    val boundSensor = sensorDatabase
        .selectByCarAndLocationFlow(carId, location)
        .distinctUntilChanged()

    override fun listen() = tyreUseCaseImpl
        .listen()
        .onStart { tyreDatabase.latestByTyreLocationByCar(location, carId)?.also { emit(it) } }
        .filter {
            val favId = boundSensor.first()?.id ?: return@filter true
            favId == it.id
        }
}

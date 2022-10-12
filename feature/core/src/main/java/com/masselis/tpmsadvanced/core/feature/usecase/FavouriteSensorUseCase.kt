package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreScope
import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@TyreScope
internal class FavouriteSensorUseCase @Inject constructor(
    private val carId: UUID,
    private val location: TyreLocation,
    private val sensorDatabase: SensorDatabase,
    private val tyreUseCaseImpl: TyreUseCaseImpl,
) : TyreUseCase {

    val found = tyreUseCaseImpl
        .listen()
        .map { Sensor(it.id, location) }
        .distinctUntilChanged()

    val saved = sensorDatabase.selectByCarAndLocationFlow(carId, location)

    suspend fun save(sensor: Sensor) = sensorDatabase.insert(sensor, carId)

    override fun listen() = tyreUseCaseImpl.listen().filter {
        val favId = saved.first()?.id ?: return@filter true
        favId == it.id
    }
}

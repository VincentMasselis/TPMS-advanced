package com.masselis.tpmsadvanced.data.car.interfaces

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.masselis.tpmsadvanced.data.car.Database
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

public class SensorDatabase @Inject internal constructor(
    database: Database
) {
    private val queries = database.sensorQueries

    public suspend fun insert(sensor: Sensor, carId: UUID): Unit = withContext(IO) {
        queries.insert(sensor.id, sensor.location, carId)
    }

    public suspend fun deleteFromCar(carId: UUID): Unit = withContext(IO) {
        queries.deleteByCar(carId)
    }

    @Suppress("NAME_SHADOWING")
    public fun selectByCarAndLocationFlow(carId: UUID, location: SensorLocation): Flow<Sensor?> =
        queries
            .selectByCarAndLocation(carId, location) { id, location, _ -> Sensor(id, location) }
            .asFlow()
            .mapToOneOrNull(IO)

    public fun countByCar(carId: UUID): Flow<Long> = queries.countByCar(carId)
        .asFlow()
        .mapToOne(IO)
}

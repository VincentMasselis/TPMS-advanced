package com.masselis.tpmsadvanced.data.car.interfaces

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.masselis.tpmsadvanced.data.car.Database
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
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

    public suspend fun delete(id: Int): Unit = withContext(IO) {
        queries.delete(id)
    }

    @Suppress("NAME_SHADOWING")
    public fun selectByCarAndLocationFlow(carId: UUID, location: TyreLocation): Flow<Sensor?> =
        queries
            .selectByCarAndLocation(carId, location) { id, location, _ -> Sensor(id, location) }
            .asFlow()
            .mapToOneOrNull(IO)
}

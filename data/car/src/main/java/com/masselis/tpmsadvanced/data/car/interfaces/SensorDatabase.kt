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

    public suspend fun upsert(sensor: Sensor, vehicleId: UUID): Unit = withContext(IO) {
        queries.upsert(sensor.id, sensor.location, vehicleId)
    }

    public suspend fun updateVehicleId(sensorId: Int, vehicleId: UUID): Unit = withContext(IO) {
        queries.updateVehicleId(vehicleId, sensorId)
    }

    public suspend fun deleteFromVehicle(vehicleId: UUID): Unit = withContext(IO) {
        queries.deleteByVehicle(vehicleId)
    }

    @Suppress("NAME_SHADOWING")
    public fun selectByVehicleAndLocationFlow(vehicleId: UUID, location: SensorLocation): Flow<Sensor?> =
        queries
            .selectByVehicleAndLocation(vehicleId, location) { id, location, _ -> Sensor(id, location) }
            .asFlow()
            .mapToOneOrNull(IO)

    public fun countByVehicle(vehicleId: UUID): Flow<Long> = queries.countByVehicle(vehicleId)
        .asFlow()
        .mapToOne(IO)
}

package com.masselis.tpmsadvanced.data.vehicle.interfaces

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

public class SensorDatabase @Inject internal constructor(
    private val database: Database,
) {
    private val vehicleQueries = database.vehicleQueries
    private val queries = database.sensorQueries

    /**
     * This method insert a sensor for a vehicle. If the sensor is already saved for an other
     * vehicle, this method moves the sensor to the new vehicle. If a sensor already exists at the
     * same location, it replaces it. This method also check the vehicle kind when before inserting
     * the sensor.
     */
    @Suppress("CyclomaticComplexMethod")
    public suspend fun upsert(
        sensor: Sensor,
        vehicleId: UUID,
    ): Unit = withContext(IO) {
        database.transaction {
            val kind = vehicleQueries.selectByUuid(vehicleId).executeAsOne().kind
            require(kind.locations.any { it == sensor.location }) {
                "Filled sensor points to a location which is not handled by the vehicle kind. Kind: $kind, sensor: $sensor"
            }
            queries.deleteByVehicleAndLocation(vehicleId, sensor.location)
            queries.upsert(sensor.id, sensor.location, vehicleId)
        }
    }

    public suspend fun deleteFromVehicle(vehicleId: UUID): Unit = withContext(IO) {
        queries.deleteByVehicle(vehicleId)
    }

    public fun selectByVehicleAndLocation(
        vehicleId: UUID,
        location: Vehicle.Kind.Location,
    ): Sensor? = queries
        .selectByVehicleAndLocation(vehicleId, location) { id, _, _ ->
            Sensor(id, location)
        }
        .executeAsOneOrNull()

    public fun selectByVehicleAndLocationFlow(
        vehicleId: UUID,
        location: Vehicle.Kind.Location,
    ): Flow<Sensor?> = queries
        .selectByVehicleAndLocation(vehicleId, location) { id, _, _ ->
            Sensor(id, location)
        }
        .asFlow()
        .mapToOneOrNull(IO)

    public fun countByVehicle(vehicleId: UUID): Flow<Long> = queries.countByVehicle(vehicleId)
        .asFlow()
        .mapToOne(IO)

    public fun selectById(id: Int): Sensor? = queries
        .selectById(id) { _, location, _ ->
            Sensor(id, location)
        }
        .executeAsOneOrNull()

    public fun selectByIdFlow(id: Int): Flow<Sensor?> = queries
        .selectById(id) { _, location, _ ->
            Sensor(id, location)
        }
        .asFlow()
        .mapToOneOrNull(IO)

    public fun selectListByVehicleId(uuid: UUID): List<Sensor> = queries
        .selectListByVehicleId(uuid) { id, location, _ ->
            Sensor(id, location)
        }
        .executeAsList()

    public fun selectListByVehicleIdFlow(uuid: UUID): Flow<List<Sensor>> = queries
        .selectListByVehicleId(uuid) { id, location, _ ->
            Sensor(id, location)
        }
        .asFlow()
        .mapToList(IO)
}

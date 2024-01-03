package com.masselis.tpmsadvanced.data.vehicle.interfaces

import app.cash.sqldelight.Query
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.util.UUID
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
                @Suppress("MaxLineLength")
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
        location: Location,
    ): Query<Sensor> = queries.selectByVehicleAndLocation(vehicleId, location, mapper)

    public fun countByVehicle(vehicleId: UUID): Query<Long> = queries.countByVehicle(vehicleId)

    public fun selectById(id: Int): Query<Sensor> = queries.selectById(id, mapper)

    public fun selectListByVehicleId(uuid: UUID): Query<Sensor> =
        queries.selectListByVehicleId(uuid, mapper)

    public fun selectListExcludingVehicleId(uuid: UUID): Query<Sensor> =
        queries.selectListExcludingVehicleId(uuid, mapper)

    private companion object {
        private val mapper: (
            id: Int,
            location: Location,
            vehicleId: UUID,
        ) -> Sensor = { id, location, _ ->
            Sensor(id, location)
        }
    }
}

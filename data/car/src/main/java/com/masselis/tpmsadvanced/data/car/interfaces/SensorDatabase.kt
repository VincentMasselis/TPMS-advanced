package com.masselis.tpmsadvanced.data.car.interfaces

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.masselis.tpmsadvanced.data.car.Database
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.*
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
     * same location, it replaces it. This method also check the vehicle kind when replacing the
     * sensor. For instance, if [vehicleId] is a motorcycle with [FRONT_LEFT] sensor, putting a new
     * [FRONT_RIGHT] sensor will automatically remove the [FRONT_LEFT] sensor because a motorcycle
     * only have 2 front wheels.
     */
    public suspend fun upsert(
        sensor: Sensor,
        vehicleId: UUID,
    ): Unit = withContext(IO) {
        database.transaction {
            val kind = vehicleQueries.selectByUuid(vehicleId).executeAsOne().kind
            fun locationToClear(vararg location: SensorLocation) {
                queries.deleteByVehicleAndLocation(vehicleId, location.toList())
            }
            when (kind) {
                Vehicle.Kind.CAR -> locationToClear(sensor.location)
                Vehicle.Kind.SINGLE_AXLE_TRAILER -> when (sensor.location) {
                    FRONT_LEFT, REAR_LEFT -> locationToClear(REAR_LEFT, FRONT_LEFT)
                    FRONT_RIGHT, REAR_RIGHT -> locationToClear(FRONT_RIGHT, REAR_RIGHT)
                }
                Vehicle.Kind.MOTORCYCLE -> when (sensor.location) {
                    FRONT_LEFT, FRONT_RIGHT -> locationToClear(FRONT_LEFT, FRONT_RIGHT)
                    REAR_LEFT, REAR_RIGHT -> locationToClear(REAR_LEFT, REAR_RIGHT)
                }
                Vehicle.Kind.TADPOLE_THREE_WHEELER -> when (sensor.location) {
                    FRONT_LEFT, FRONT_RIGHT -> locationToClear(sensor.location)
                    REAR_LEFT, REAR_RIGHT -> locationToClear(REAR_LEFT, REAR_RIGHT)
                }
                Vehicle.Kind.DELTA_THREE_WHEELER -> when (sensor.location) {
                    FRONT_LEFT, FRONT_RIGHT -> locationToClear(FRONT_LEFT, FRONT_RIGHT)
                    REAR_LEFT, REAR_RIGHT -> locationToClear(sensor.location)
                }
            }
            queries.upsert(sensor.id, sensor.location, vehicleId)
        }
    }

    public suspend fun deleteFromVehicle(vehicleId: UUID): Unit = withContext(IO) {
        queries.deleteByVehicle(vehicleId)
    }

    @Suppress("NAME_SHADOWING")
    public fun selectByVehicleAndLocationFlow(
        vehicleId: UUID,
        vararg location: SensorLocation
    ): Flow<Sensor?> = queries
        .selectByVehicleAndLocation(vehicleId, location.toList()) { id, location, _ ->
            Sensor(
                id,
                location
            )
        }
        .asFlow()
        .mapToOneOrNull(IO)

    public fun countByVehicle(vehicleId: UUID): Flow<Long> = queries.countByVehicle(vehicleId)
        .asFlow()
        .mapToOne(IO)
}

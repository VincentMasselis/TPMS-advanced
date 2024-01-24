package com.masselis.tpmsadvanced.data.vehicle.interfaces

import android.database.sqlite.SQLiteConstraintException
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.CAR
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.DELTA_THREE_WHEELER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.SINGLE_AXLE_TRAILER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.TADPOLE_THREE_WHEELER

@Suppress("LongMethod", "CyclomaticComplexMethod", "MagicNumber")
internal fun Database.Companion.afterVersion3(
    locationAdapter: ColumnAdapter<Vehicle.Kind.Location, Long>
) = AfterVersion(3) { driver ->
    driver
        .executeQuery(
            null,
            "SELECT location, vehicleId FROM Sensor",
            {
                val result = mutableListOf<Pair<SensorLocation, String>>()
                while (it.next().value)
                    result += Pair(
                        SensorLocation.entries[it.getLong(0)!!.toInt()],
                        it.getString(1)!!
                    )
                QueryResult.Value(result.toList())
            },
            0,
        )
        .value
        .forEach { (sensorLocation, vehicleUuid) ->
            driver
                .executeQuery(
                    identifier = null,
                    sql = "SELECT kind FROM Vehicle WHERE Vehicle.uuid = ?",
                    parameters = 1,
                    binders = { bindString(0, vehicleUuid) },
                    mapper = { cursor ->
                        cursor.next()
                        cursor.getString(0)!!
                            .let { Vehicle.Kind.valueOf(it) }
                            .let { QueryResult.Value(it) }
                    },
                )
                .value
                .let { kind ->
                    when (kind) {
                        CAR ->
                            Vehicle.Kind.Location.Wheel(sensorLocation)

                        SINGLE_AXLE_TRAILER -> when (sensorLocation) {
                            FRONT_LEFT, REAR_LEFT -> Vehicle.Kind.Location.Side(LEFT)
                            FRONT_RIGHT, REAR_RIGHT -> Vehicle.Kind.Location.Side(RIGHT)
                        }

                        MOTORCYCLE -> when (sensorLocation) {
                            FRONT_LEFT, FRONT_RIGHT -> Vehicle.Kind.Location.Axle(FRONT)
                            REAR_LEFT, REAR_RIGHT -> Vehicle.Kind.Location.Axle(REAR)
                        }

                        TADPOLE_THREE_WHEELER -> when (sensorLocation) {
                            FRONT_LEFT, FRONT_RIGHT -> Vehicle.Kind.Location.Wheel(sensorLocation)
                            REAR_LEFT, REAR_RIGHT -> Vehicle.Kind.Location.Axle(REAR)
                        }

                        DELTA_THREE_WHEELER -> when (sensorLocation) {
                            FRONT_LEFT, FRONT_RIGHT -> Vehicle.Kind.Location.Axle(FRONT)
                            REAR_LEFT, REAR_RIGHT -> Vehicle.Kind.Location.Wheel(sensorLocation)
                        }
                    }
                }
                .let(locationAdapter::encode)
                .let { encodedLocation ->
                    try {
                        driver.execute(
                            null,
                            "UPDATE Sensor SET location = ? WHERE vehicleId = ?",
                            2
                        ) {
                            bindLong(0, encodedLocation)
                            bindString(1, vehicleUuid)
                        }
                    } catch (_: SQLiteConstraintException) {
                        // Appends if a vehicle like a motorcycle has 2 front wheels associated.
                        // The new database schema version 4 doesn't accept this case so the first
                        // sensor is set as the front wheel, the 2nd one is dropped from the
                        // database.
                    }
                    driver.execute(
                        null,
                        "UPDATE Tyre SET location = ? WHERE vehicleId = ?",
                        2
                    ) {
                        bindLong(0, encodedLocation)
                        bindString(1, vehicleUuid)
                    }
                }
        }
}

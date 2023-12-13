package com.masselis.tpmsadvanced.data.vehicle.interfaces

import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

public class TyreDatabase @Inject internal constructor(
    database: Database
) {
    private val queries = database.tyreQueries

    public suspend fun insert(tyre: Tyre.Located, vehicleId: UUID): Unit = withContext(IO) {
        queries.insert(
            tyre.id,
            tyre.timestamp,
            tyre.rssi,
            tyre.location,
            tyre.pressure,
            tyre.temperature,
            tyre.battery,
            tyre.isAlarm,
            vehicleId
        )
    }

    public fun latestByTyreLocationByVehicle(
        location: Location,
        vehicleId: UUID
    ): Tyre.Located? = queries
        .latestByTyreLocationByVehicle(
            location,
            vehicleId
        ) { id, timestamp, rssi, _, pressure, temperature, battery, isAlarm ->
            Tyre.Located(timestamp, rssi, id, pressure, temperature, battery, isAlarm, location)
        }
        .executeAsOneOrNull()
}

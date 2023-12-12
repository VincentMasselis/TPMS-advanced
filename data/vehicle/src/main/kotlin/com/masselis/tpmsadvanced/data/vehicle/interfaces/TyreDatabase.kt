package com.masselis.tpmsadvanced.data.vehicle.interfaces

import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

public class TyreDatabase @Inject internal constructor(
    database: Database
) {
    private val queries = database.tyreQueries

    public suspend fun insert(tyre: Tyre, vehicleId: UUID): Unit = withContext(IO) {
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
        locations: Set<SensorLocation>,
        vehicleId: UUID
    ): Tyre? = queries
        .latestByTyreLocationByVehicle(
            locations,
            vehicleId
        ) { id, timestamp, rssi, location, pressure, temperature, battery, isAlarm ->
            Tyre(timestamp, rssi, location, id, pressure, temperature, battery, isAlarm)
        }
        .executeAsOneOrNull()
}

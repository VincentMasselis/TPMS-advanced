package com.masselis.tpmsadvanced.data.vehicle.interfaces

import com.masselis.tpmsadvanced.core.database.QueryList
import com.masselis.tpmsadvanced.core.database.QueryList.Companion.asList
import com.masselis.tpmsadvanced.core.database.QueryOneOrNull
import com.masselis.tpmsadvanced.core.database.QueryOneOrNull.Companion.asOneOrNull
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
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
            tyre.sensorId,
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
    ): QueryOneOrNull<Tyre.Located> = queries
        .latestByTyreLocationByVehicle(
            location,
            vehicleId
        ) { id, timestamp, rssi, _, pressure, temperature, battery, isAlarm ->
            Tyre.Located(timestamp, rssi, id, pressure, temperature, battery, isAlarm, location)
        }
        .asOneOrNull()

    public fun selectListByVehicle(vehicleId: UUID): QueryList<Tyre.Located> = queries
        .selectListByVehicle(vehicleId){ id, timestamp, rssi, location, pressure, temperature, battery, isAlarm ->
            Tyre.Located(timestamp, rssi, id, pressure, temperature, battery, isAlarm, location)
        }.asList()

}

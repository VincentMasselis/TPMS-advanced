package com.masselis.tpmsadvanced.data.car.interfaces

import com.masselis.tpmsadvanced.data.car.Database
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Tyre
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

public class TyreDatabase @Inject internal constructor(
    database: Database
) {
    private val queries = database.tyreQueries

    public suspend fun insert(tyre: Tyre, sensorId: Int): Unit = withContext(IO) {
        queries.insert(
            tyre.id,
            tyre.timestamp,
            tyre.location,
            tyre.pressure,
            tyre.temperature,
            tyre.battery,
            tyre.isAlarm,
            sensorId
        )
    }

    public fun latestByTyreLocationByCar(location: SensorLocation, carId: UUID): Tyre? = queries
        .latestByTyreLocationByCar(
            location,
            carId
        ) { id, timestamp, pressure, temperature, battery, isAlarm ->
            Tyre(timestamp, location, id, pressure, temperature, battery, isAlarm)
        }
        .executeAsOneOrNull()
}

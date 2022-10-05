package com.masselis.tpmsadvanced.data.car.interfaces

import com.masselis.tpmsadvanced.data.car.Database
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.masselis.tpmsadvanced.data.car.model.Sensor as SensorModel

public class SensorDatabase @Inject internal constructor(
    database: Database
) {
    private val queries = database.sensorQueries

    public suspend fun insert(sensor: SensorModel, carId: String): Unit = withContext(IO) {
        queries.insert(sensor.id, sensor.location, carId)
    }

    public suspend fun delete(id: Int): Unit = withContext(IO) {
        queries.delete(id)
    }
}

package com.masselis.tpmsadvanced.data.car.interfaces

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.masselis.tpmsadvanced.data.car.Database
import com.masselis.tpmsadvanced.data.car.model.Car
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import dagger.Reusable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@Reusable
public class CarDatabase @Inject internal constructor(database: Database) {

    private val queries = database.carQueries

    public suspend fun insert(id: UUID, name: String, isFavourite: Boolean): Unit =
        withContext(IO) {
            queries.insert(id, name, isFavourite)
        }

    public suspend fun setAsFavourite(uuid: UUID, isFavourite: Boolean): Unit = withContext(IO) {
        queries.setAsFavourite(isFavourite, uuid)
    }

    public fun selectLowPressure(carId: UUID): Pressure =
        queries.selectLowPressureByCarId(carId).executeAsOne()

    public suspend fun updateLowPressure(lowPressure: Pressure, carId: UUID): Unit =
        withContext(IO) {
            queries.updateLowPressure(lowPressure, carId)
        }

    public fun selectHighPressure(carId: UUID): Pressure =
        queries.selectHighPressureByCarId(carId).executeAsOne()

    public suspend fun updateHighPressure(highPressure: Pressure, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateHighPressure(highPressure, uuid)
        }

    public fun selectLowTemp(carId: UUID): Temperature =
        queries.selectLowTempByCarId(carId).executeAsOne()

    public suspend fun updateLowTemp(lowTemp: Temperature, uuid: UUID): Unit = withContext(IO) {
        queries.updateLowTemp(lowTemp, uuid)
    }

    public fun selectNormalTemp(carId: UUID): Temperature =
        queries.selectNormalTempByCarId(carId).executeAsOne()

    public suspend fun updateNormalTemp(normalTemp: Temperature, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateNormalTemp(normalTemp, uuid)
        }

    public fun selectHighTemp(carId: UUID): Temperature =
        queries.selectHighTempByCarId(carId).executeAsOne()

    public suspend fun updateHighTemp(highTemp: Temperature, uuid: UUID): Unit = withContext(IO) {
        queries.updateHighTemp(highTemp, uuid)
    }

    public suspend fun delete(uuid: UUID): Unit = withContext(IO) {
        queries.delete(uuid)
    }

    public fun currentFavouriteFlow(): Flow<Car> = queries.currentFavourite(mapper)
        .asFlow()
        .mapToOne(IO)

    public fun currentFavourite(): Car = queries.currentFavourite(mapper).executeAsOne()

    public fun selectAllFlow(): Flow<List<Car>> = queries.selectAll(mapper)
        .asFlow()
        .mapToList(IO)

    public fun selectAll(): List<Car> = queries.selectAll(mapper).executeAsList()

    public fun selectByUuid(carId: UUID): Flow<Car> = queries.selectByUuid(carId, mapper)
        .asFlow()
        .mapToOne(IO)

    private companion object {
        val mapper: (UUID, String, Boolean, Pressure, Pressure, Temperature, Temperature, Temperature) -> Car =
            { uuid, name, isFavourite, lowPressure, highPressure, lowTemp, normalTemp, highTemp ->
                Car(
                    uuid,
                    name,
                    isFavourite,
                    lowPressure,
                    highPressure,
                    lowTemp,
                    normalTemp,
                    highTemp
                )
            }
    }
}

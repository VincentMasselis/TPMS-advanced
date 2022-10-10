package com.masselis.tpmsadvanced.data.car.interfaces

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.masselis.tpmsadvanced.data.car.Car
import com.masselis.tpmsadvanced.data.car.Database
import dagger.Reusable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@Reusable
public class CarDatabase @Inject internal constructor(database: Database) {

    private val queries = database.carQueries

    public suspend fun insert(car: Car): Unit = withContext(IO) {
        queries.insert(car.uuid, car.name, car.isFavourite)
    }

    public suspend fun setAsFavourite(uuid: UUID, isFavourite: Boolean): Unit = withContext(IO) {
        queries.setAsFavourite(isFavourite, uuid)
    }

    public suspend fun delete(uuid: UUID): Unit = withContext(IO) {
        queries.delete(uuid)
    }

    public fun currentFavouriteFlow(): Flow<Car> = queries.currentFavourite()
        .asFlow()
        .mapToOne(IO)

    public fun currentFavourite(): Car = queries.currentFavourite().executeAsOne()

    public fun selectAllFlow(): Flow<List<Car>> = queries.selectAll()
        .asFlow()
        .mapToList(IO)

    public fun selectAll(): List<Car> = queries.selectAll().executeAsList()
}

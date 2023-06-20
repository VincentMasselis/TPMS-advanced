package com.masselis.tpmsadvanced.data.car.interfaces

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.masselis.tpmsadvanced.data.car.Database
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import dagger.Reusable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@Suppress("TooManyFunctions")
@Reusable
public class VehicleDatabase @Inject internal constructor(database: Database) {

    private val queries = database.vehicleQueries

    public suspend fun insert(
        id: UUID,
        kind: Vehicle.Kind,
        name: String,
        isCurrent: Boolean
    ): Unit =
        withContext(IO) { queries.insert(id, kind, name, isCurrent) }

    public suspend fun setIsCurrent(uuid: UUID, isCurrent: Boolean): Unit = withContext(IO) {
        queries.setAsFavourite(isCurrent, uuid)
    }

    public fun selectLowPressure(vehicleId: UUID): Pressure =
        queries.selectLowPressureByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateLowPressure(lowPressure: Pressure, vehicleId: UUID): Unit =
        withContext(IO) {
            queries.updateLowPressure(lowPressure, vehicleId)
        }

    public fun selectHighPressure(vehicleId: UUID): Pressure =
        queries.selectHighPressureByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateHighPressure(highPressure: Pressure, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateHighPressure(highPressure, uuid)
        }

    public fun selectLowTemp(vehicleId: UUID): Temperature =
        queries.selectLowTempByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateLowTemp(lowTemp: Temperature, uuid: UUID): Unit = withContext(IO) {
        queries.updateLowTemp(lowTemp, uuid)
    }

    public fun selectNormalTemp(vehicleId: UUID): Temperature =
        queries.selectNormalTempByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateNormalTemp(normalTemp: Temperature, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateNormalTemp(normalTemp, uuid)
        }

    public fun selectHighTemp(vehicleId: UUID): Temperature =
        queries.selectHighTempByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateHighTemp(highTemp: Temperature, uuid: UUID): Unit = withContext(IO) {
        queries.updateHighTemp(highTemp, uuid)
    }

    public suspend fun prepareDelete(uuid: UUID): Unit = withContext(IO) {
        queries.updateIsDeleting(true, uuid)
    }

    public suspend fun delete(uuid: UUID): Unit = withContext(IO) {
        queries.delete(uuid)
    }

    public fun selectIsBackgroundMonitor(uuid: UUID): Boolean = queries
        .selectIsBackgroundMonitor(uuid)
        .executeAsOne()

    public fun selectIsBackgroundMonitorFlow(uuid: UUID): Flow<Boolean> = queries
        .selectIsBackgroundMonitor(uuid)
        .asFlow()
        .mapToOne(IO)

    public suspend fun updateIsBackgroundMonitor(isBackgroundMonitor: Boolean, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateIsBackgroundMonitor(isBackgroundMonitor, uuid)
        }

    public suspend fun updateIsBackgroundMonitorList(
        isBackgroundMonitor: Boolean,
        uuids: List<UUID>
    ): Unit =
        withContext(IO) {
            queries.updateIsBackgroundMonitorList(isBackgroundMonitor, uuids)
        }

    public suspend fun updateEveryIsBackgroundMonitorToFalse(): Unit = withContext(IO) {
        queries.updateEveryIsBackgroundMonitorToFalse()
    }

    public fun currentVehicleFlow(): Flow<Vehicle> = queries.currentFavourite(mapper)
        .asFlow()
        .mapToOne(IO)

    public fun currentVehicle(): Vehicle = queries.currentFavourite(mapper).executeAsOne()

    public fun count(): Long = queries.count().executeAsOne()

    public fun countFlow(): Flow<Long> = queries.count().asFlow().mapToOne(IO)

    public fun selectAllFlow(): Flow<List<Vehicle>> = queries.selectAll(mapper)
        .asFlow()
        .mapToList(IO)

    public fun selectAll(): List<Vehicle> = queries.selectAll(mapper).executeAsList()

    public fun selectByUuidFlow(vehicleId: UUID): Flow<Vehicle> =
        queries.selectByUuid(vehicleId, mapper)
            .asFlow()
            .mapToOne(IO)

    public fun selectBySensorId(sensorId: Int): Flow<Vehicle?> = queries
        .selectBySensorId(sensorId, mapper)
        .asFlow()
        .mapToOneOrNull(IO)

    private companion object {
        val mapper: (
            UUID,
            String,
            Boolean,
            Pressure,
            Pressure,
            Temperature,
            Temperature,
            Temperature,
            Vehicle.Kind,
            Boolean,
            Boolean,
        ) -> Vehicle =
            { uuid, name, _, lowPressure, highPressure, lowTemp, normalTemp, highTemp, kind, _, isBackgroundMonitor ->
                Vehicle(
                    uuid,
                    kind,
                    name,
                    lowPressure,
                    highPressure,
                    lowTemp,
                    normalTemp,
                    highTemp,
                    isBackgroundMonitor,
                )
            }
    }
}

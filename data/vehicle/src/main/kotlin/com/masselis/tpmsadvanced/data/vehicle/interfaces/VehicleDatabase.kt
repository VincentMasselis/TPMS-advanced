package com.masselis.tpmsadvanced.data.vehicle.interfaces

import app.cash.sqldelight.Query
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.Reusable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@Suppress("TooManyFunctions")
@Reusable
public class VehicleDatabase @Inject internal constructor(database: Database) {

    private val queries = database.vehicleQueries

    init {
        // Cleanup the database at launch. There is no risk of useless multiples calls since
        // VehicleDatabase is annotated `@Reusable`.
        queries.deleteIsDeleting()
    }

    public suspend fun insert(
        id: UUID,
        kind: Vehicle.Kind,
        name: String,
        isCurrent: Boolean
    ): Unit = withContext(IO) {
        queries.insert(id, kind, name, isCurrent)
    }

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

    public suspend fun setIsDeleting(uuid: UUID): Unit = withContext(IO) {
        queries.updateIsDeleting(true, uuid)
    }

    public suspend fun delete(uuid: UUID): Unit = withContext(IO) {
        queries.delete(uuid)
    }

    public fun selectIsBackgroundMonitor(uuid: UUID): Query<Boolean> = queries
        .selectIsBackgroundMonitor(uuid)

    public suspend fun updateIsBackgroundMonitor(
        isBackgroundMonitor: Boolean,
        uuid: UUID
    ): Unit = withContext(IO) {
        queries.updateIsBackgroundMonitor(isBackgroundMonitor, uuid)
    }

    public suspend fun updateIsBackgroundMonitorList(
        isBackgroundMonitor: Boolean,
        uuids: List<UUID>
    ): Unit = withContext(IO) {
        queries.updateIsBackgroundMonitorList(isBackgroundMonitor, uuids)
    }

    public suspend fun updateEveryIsBackgroundMonitorToFalse(): Unit = withContext(IO) {
        queries.updateEveryIsBackgroundMonitorToFalse()
    }

    public fun currentVehicle(): Query<Vehicle> = queries.currentFavourite(mapper)

    public fun count(): Query<Long> = queries.count()

    public fun selectUuidIsDeleting(): Query<UUID> = queries.selectUuidIsDeleting()

    public fun selectAll(): Query<Vehicle> = queries.selectAll(mapper)

    public fun selectByUuid(vehicleId: UUID): Query<Vehicle> = queries
        .selectByUuid(vehicleId, mapper)

    public fun selectBySensorId(sensorId: Int): Query<Vehicle> = queries
        .selectBySensorId(sensorId, mapper)

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

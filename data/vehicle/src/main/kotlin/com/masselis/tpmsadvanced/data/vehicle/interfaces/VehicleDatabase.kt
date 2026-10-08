package com.masselis.tpmsadvanced.data.vehicle.interfaces

import app.cash.sqldelight.Query
import com.masselis.tpmsadvanced.core.database.QueryList
import com.masselis.tpmsadvanced.core.database.QueryList.Companion.asList
import com.masselis.tpmsadvanced.core.database.QueryOne
import com.masselis.tpmsadvanced.core.database.QueryOne.Companion.asOne
import com.masselis.tpmsadvanced.core.database.QueryOneOrNull
import com.masselis.tpmsadvanced.core.database.QueryOneOrNull.Companion.asOneOrNull
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.time.Instant

@Suppress("TooManyFunctions")
public class VehicleDatabase internal constructor(database: Database) {

    private val queries = database.vehicleQueries

    init {
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

    public fun selectSeparateFrontRearPressure(vehicleId: UUID): Query<Boolean> = queries
        .selectSeparateFrontRearPressureByVehicleId(vehicleId)

    public suspend fun updateSeparateFrontRearPressure(
        separateFrontRearPressure: Boolean,
        vehicleId: UUID
    ): Unit = withContext(IO) {
        queries.updateSeparateFrontRearPressure(separateFrontRearPressure, vehicleId)
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

    public fun selectRearLowPressure(vehicleId: UUID): Pressure =
        queries.selectRearLowPressureByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateRearLowPressure(lowPressure: Pressure, vehicleId: UUID): Unit =
        withContext(IO) {
            queries.updateRearLowPressure(lowPressure, vehicleId)
        }

    public fun selectRearHighPressure(vehicleId: UUID): Pressure =
        queries.selectRearHighPressureByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateRearHighPressure(highPressure: Pressure, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateRearHighPressure(highPressure, uuid)
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

    public fun selectLastFrontRearTyreSwapFlow(
        vehicleId: UUID
    ): Flow<Instant?> = queries
        .selectLastFrontRearTyreSwap(vehicleId)
        .asOne()
        .asFlow()
        .map { it.lastFrontRearTyreSwap }

    public suspend fun updateLastFrontRearTyreSwap(
        lastFrontRearTyreSwap: Instant?,
        vehicleId: UUID
    ): Unit = withContext(IO) {
        queries.updateLastFrontRearTyreSwap(lastFrontRearTyreSwap, vehicleId)
    }

    public suspend fun setIsDeleting(uuid: UUID): Unit = withContext(IO) {
        queries.updateIsDeleting(true, uuid)
    }

    public suspend fun delete(uuid: UUID): Unit = withContext(IO) {
        queries.delete(uuid)
    }

    public fun currentVehicle(): QueryOne<Vehicle> = queries.currentFavourite(mapper).asOne()

    public fun count(): QueryOne<Long> = queries.count().asOne()

    public fun selectUuidIsDeleting(): QueryList<UUID> = queries.selectUuidIsDeleting().asList()

    public fun selectAll(): QueryList<Vehicle> = queries.selectAll(mapper).asList()

    public fun selectByUuid(vehicleId: UUID): QueryOne<Vehicle> = queries
        .selectByUuid(vehicleId, mapper)
        .asOne()

    public fun selectBySensorId(sensorId: Int): QueryOneOrNull<Vehicle> = queries
        .selectBySensorId(sensorId, mapper)
        .asOneOrNull()

    private companion object {
        @Suppress("MaxLineLength")
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
            Pressure,
            Pressure,
            Instant?,
        ) -> Vehicle =
            { uuid, name, _, lowPressure, highPressure, lowTemp, normalTemp, highTemp, kind, _, separateFrontRearPressure, rearLowPressure, rearHighPressure, lastFrontRearTyreSwap ->
                Vehicle(
                    uuid,
                    kind,
                    name,
                    lowPressure,
                    highPressure,
                    separateFrontRearPressure,
                    rearLowPressure,
                    rearHighPressure,
                    lowTemp,
                    normalTemp,
                    highTemp,
                    lastFrontRearTyreSwap,
                )
            }
    }
}

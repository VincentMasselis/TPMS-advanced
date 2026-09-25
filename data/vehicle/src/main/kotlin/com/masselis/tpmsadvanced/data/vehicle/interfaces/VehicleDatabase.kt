package com.masselis.tpmsadvanced.data.vehicle.interfaces

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
import kotlinx.coroutines.withContext
import java.util.UUID

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
        isCurrent: Boolean,
        defaultsToSeparateFrontRearPressure: Boolean = false,
    ): Unit = withContext(IO) {
        // Runs as a single transaction so a reactive observer (e.g. a settings screen navigated
        // to right after creation) never sees the row with the rear override half-applied.
        queries.transaction {
            queries.insert(id, kind, name, isCurrent)
            if (defaultsToSeparateFrontRearPressure) {
                val lowPressure = queries.selectLowPressureByVehicleId(id).executeAsOne()
                val highPressure = queries.selectHighPressureByVehicleId(id).executeAsOne()
                queries.updateRearLowPressure(lowPressure, id)
                queries.updateRearHighPressure(highPressure, id)
                queries.updateSeparateRearPressure(true, id)
            }
        }
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

    public fun selectRearLowPressure(vehicleId: UUID): Pressure? =
        queries.selectRearLowPressureByVehicleId(vehicleId).executeAsOne().rearLowPressure

    public suspend fun updateRearLowPressure(rearLowPressure: Pressure?, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateRearLowPressure(rearLowPressure, uuid)
        }

    public fun selectRearHighPressure(vehicleId: UUID): Pressure? =
        queries.selectRearHighPressureByVehicleId(vehicleId).executeAsOne().rearHighPressure

    public suspend fun updateRearHighPressure(rearHighPressure: Pressure?, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateRearHighPressure(rearHighPressure, uuid)
        }

    public fun selectSeparateRearPressure(vehicleId: UUID): Boolean =
        queries.selectSeparateRearPressureByVehicleId(vehicleId).executeAsOne()

    public suspend fun updateSeparateRearPressure(separate: Boolean, uuid: UUID): Unit =
        withContext(IO) {
            queries.updateSeparateRearPressure(separate, uuid)
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
            Pressure?,
            Pressure?,
            Boolean,
        ) -> Vehicle =
            { uuid, name, _, lowPressure, highPressure, lowTemp, normalTemp, highTemp, kind, _,
              rearLowPressure, rearHighPressure, separateRearPressure ->
                Vehicle(
                    uuid,
                    kind,
                    name,
                    lowPressure,
                    highPressure,
                    lowTemp,
                    normalTemp,
                    highTemp,
                    rearLowPressure,
                    rearHighPressure,
                    separateRearPressure,
                )
            }
    }
}

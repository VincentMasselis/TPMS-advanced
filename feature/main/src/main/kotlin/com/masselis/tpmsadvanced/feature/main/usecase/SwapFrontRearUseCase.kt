package com.masselis.tpmsadvanced.feature.main.usecase

import co.touchlab.kermit.Logger
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.CAR
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.time.Clock

internal class SwapFrontRearUseCase(
    private val vehicle: Vehicle,
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
) {

    private val logger = Logger.withTag("SwapFrontRearUseCase")

    fun lastSwap() = vehicleDatabase
        .selectLastFrontRearTyreSwapFlow(vehicle.uuid)
        .distinctUntilChanged()

    fun canSwap() =
        if (vehicle.kind != CAR) flowOf(false)
        else sensorDatabase
            .selectListByVehicleId(vehicle.uuid)
            .asFlow(IO)
            .map { sensors -> sensors.sortedBy { it.id } }
            .distinctUntilChanged()
            .map { sensors -> sensors.map { it.location }.toSet() }
            .onEach { sensors -> logger.i { "Bound sensors locations: ${sensors.joinToString { it.toString() }}" } }
            .map { locations ->
                @Suppress("MagicNumber")
                locations.containsAll(listOf(FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT))
                        && locations.size == 4
            }

    suspend fun swap() {
        @Suppress("MaxLineLength")
        require(canSwap().first()) { "Cannot swap because the bound sensors doesn't match the requirement or the vehicle is not a CAR" }

        fun MutableList<Sensor>.swap(first: Location.Wheel, second: Location.Wheel) {
            val firstIndex = indexOfFirst { it.location == first }
            val secondIndex = indexOfFirst { it.location == second }
            get(firstIndex)
                .copy(location = second)
                .also { set(firstIndex, it) }
            get(secondIndex)
                .copy(location = first)
                .also { set(secondIndex, it) }
        }

        sensorDatabase.selectListByVehicleId(vehicle.uuid)
            .execute()
            .toMutableList()
            .also { sensors ->
                sensors.swap(FRONT_LEFT, REAR_LEFT)
                sensors.swap(FRONT_RIGHT, REAR_RIGHT)
            }
            .toList()
            .also { swappedSensors ->
                sensorDatabase.deleteFromVehicleAndUpsert(swappedSensors, vehicle.uuid)
            }
        vehicleDatabase.updateLastFrontRearTyreSwap(Clock.System.now(), vehicle.uuid)
    }
}

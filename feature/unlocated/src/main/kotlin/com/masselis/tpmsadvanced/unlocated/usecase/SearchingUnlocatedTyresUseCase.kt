package com.masselis.tpmsadvanced.unlocated.usecase

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Suppress("OPT_IN_USAGE")
internal class SearchingUnlocatedTyresUseCase @AssistedInject constructor(
    private val scanner: BluetoothLeScanner,
    private val sensorDatabase: SensorDatabase,
    private val vehicleDatabase: VehicleDatabase,
    @Assisted private val vehicleUuid: UUID,
) {

    @AssistedFactory
    internal interface Factory : (UUID) -> SearchingUnlocatedTyresUseCase


    /**
     * Returns [Sensor] which are bound to the current vehicle or [Tyre.Unlocated] which are not
     * bound and ready to be bound to the current vehicle.
     */
    fun search() = combine(
        sensorDatabase.selectListByVehicleId(vehicleUuid).asFlow(),
        sensorDatabase.selectListExcludingVehicleId(vehicleUuid).asFlow(),
        scanner.highDutyScan()
            .mapNotNull { it as? Tyre.Unlocated }
            .scan(mutableListOf<Tyre.Unlocated>()) { acc, value ->
                val index = acc.indexOfFirst { it.sensorId == value.sensorId }
                if (index == -1) acc += value
                else acc[index] = value
                acc.sortByDescending { it.rssi }
                acc
            },
    ) { boundedSensorsForCurrentVehicle, boundedSensorsForOtherVehicles, foundTyres ->
        val boundedSensors = boundedSensorsForCurrentVehicle + boundedSensorsForOtherVehicles
        Triple(
            boundedSensorsForCurrentVehicle.map { sensor ->
                sensor to foundTyres.firstOrNull { it.sensorId == sensor.id }
            },
            foundTyres.filter { tyre -> boundedSensors.none { it.id == tyre.sensorId } },
            foundTyres.mapNotNull { tyre ->
                boundedSensorsForOtherVehicles.firstOrNull { it.id == tyre.sensorId }
                    .let { it ?: return@mapNotNull null }
                    .let { it to tyre }
            }
        )
    }.flatMapLatest { (boundSensorToCurrentVehicle, unboundTyres, boundSensorAndTyres) ->
        if (boundSensorAndTyres.isEmpty())
            flowOf(Result(boundSensorToCurrentVehicle, unboundTyres, emptyList()))
        else
            combine(
                boundSensorAndTyres.map { (sensor) ->
                    vehicleDatabase.selectBySensorId(sensor.id).asFlow().map { it!! }
                }
            ) { boundVehicles ->
                Result(
                    boundSensorToCurrentVehicle,
                    unboundTyres,
                    boundVehicles.mapIndexed { index, boundVehicle ->
                        val (boundSensor, boundTyre) = boundSensorAndTyres[index]
                        Triple(boundVehicle, boundSensor, boundTyre)
                    }
                )
            }.flowOn(IO)
    }

    @Parcelize
    data class Result(
        val boundSensorToCurrentVehicle: List<Pair<Sensor, Tyre.Unlocated?>>,
        val unboundTyres: List<Tyre.Unlocated>,
        val boundTyresToOtherVehicle: List<Triple<Vehicle, Sensor, Tyre.Unlocated>>,
    ) : Parcelable
}

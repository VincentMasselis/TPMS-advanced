package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus

internal interface SensorBindingUseCase {

    fun boundSensor(): StateFlow<Sensor?>
    fun boundVehicle(sensor: Sensor): Flow<Vehicle?>

    suspend fun bind(sensor: Sensor)

    class Impl(
        private val currentVehicle: Vehicle,
        private val vehicleDatabase: VehicleDatabase,
        private val sensorDatabase: SensorDatabase,
        currentLocation: Location,
        scope: CoroutineScope,
    ) : SensorBindingUseCase {

        private val boundSensor = sensorDatabase.selectByVehicleAndLocation(
            currentVehicle.uuid,
            currentLocation
        ).asStateFlow(scope + IO, Eagerly)

        override fun boundSensor(): StateFlow<Sensor?> = boundSensor

        override fun boundVehicle(sensor: Sensor) = vehicleDatabase
            .selectBySensorId(sensor.id)
            .asFlow()

        override suspend fun bind(sensor: Sensor) =
            sensorDatabase.upsert(sensor, currentVehicle.uuid)
    }

    object NoOp : SensorBindingUseCase {
        override fun boundSensor(): StateFlow<Sensor?> = MutableStateFlow(null)
        override fun boundVehicle(sensor: Sensor): Flow<Vehicle?> = MutableStateFlow(null)
        override suspend fun bind(sensor: Sensor) {}
    }
}

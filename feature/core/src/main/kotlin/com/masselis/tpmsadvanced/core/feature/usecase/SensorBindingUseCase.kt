package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Named

@TyreComponent.Scope
internal class SensorBindingUseCase @Inject constructor(
    @Named("base") private val currentVehicle: Vehicle,
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
    currentLocation: Location,
    scope: CoroutineScope,
) {

    private val boundSensor = sensorDatabase.selectByVehicleAndLocation(
        currentVehicle.uuid,
        currentLocation
    ).asStateFlow(scope + IO, Eagerly)

    fun boundSensor(): StateFlow<Sensor?> = boundSensor

    fun boundVehicle(sensor: Sensor) = vehicleDatabase.selectBySensorId(sensor.id).asFlow()

    suspend fun bind(sensor: Sensor) = sensorDatabase.upsert(sensor, currentVehicle.uuid)
}

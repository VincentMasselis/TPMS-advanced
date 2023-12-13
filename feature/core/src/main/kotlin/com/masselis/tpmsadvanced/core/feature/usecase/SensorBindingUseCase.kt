package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Named

internal class SensorBindingUseCase @Inject constructor(
    @Named("base") private val currentVehicle: Vehicle,
    private val currentLocation: Location,
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
) {
    fun boundSensorFlow() = sensorDatabase
        .selectByVehicleAndLocationFlow(currentVehicle.uuid, currentLocation)
        .distinctUntilChanged()

    suspend fun boundSensor() = withContext(IO) {
        sensorDatabase.selectByVehicleAndLocation(currentVehicle.uuid, currentLocation)
    }

    fun boundVehicle(sensor: Sensor) = vehicleDatabase.selectBySensorIdFlow(sensor.id)

    suspend fun bind(sensor: Sensor) = sensorDatabase.upsert(sensor, currentVehicle.uuid)
}

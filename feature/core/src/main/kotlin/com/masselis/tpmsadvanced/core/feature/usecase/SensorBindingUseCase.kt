package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*
import javax.inject.Inject
import javax.inject.Named

internal class SensorBindingUseCase @Inject constructor(
    @Named("base") private val vehicle: Vehicle,
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
    private val locations: Set<SensorLocation>,
) {
    @Suppress("SpreadOperator")
    fun boundSensor() = sensorDatabase
        .selectByVehicleAndLocationFlow(vehicle.uuid, *locations.toTypedArray())
        .distinctUntilChanged()

    fun boundVehicle(sensor: Sensor) = vehicleDatabase.selectBySensorId(sensor.id)

    suspend fun bind(sensor: Sensor) = sensorDatabase.upsert(sensor, vehicle.uuid)
}

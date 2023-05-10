package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
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

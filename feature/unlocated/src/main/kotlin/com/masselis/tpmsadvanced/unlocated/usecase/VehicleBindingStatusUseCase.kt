package com.masselis.tpmsadvanced.unlocated.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class VehicleBindingStatusUseCase @Inject constructor(
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
) {
    fun areAllWheelBound(vehicle: Vehicle) = vehicleDatabase
        .selectByUuid(vehicle.uuid)
        .map { it.kind }
        .flatMapLatest { kind ->
            sensorDatabase.selectListByVehicleId(vehicle.uuid)
                .map { sensors -> sensors.map { it.location } }
                .map { it.toSet() }
                .map { locations -> kind.locations.subtract(locations).isEmpty() }
        }
        .distinctUntilChanged()
}
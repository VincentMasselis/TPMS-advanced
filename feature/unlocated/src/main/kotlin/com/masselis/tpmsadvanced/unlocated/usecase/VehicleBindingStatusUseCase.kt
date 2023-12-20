package com.masselis.tpmsadvanced.unlocated.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class VehicleBindingStatusUseCase @Inject constructor(
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
) {
    fun areAllWheelBound(vehicleUuid: UUID) = vehicleDatabase
        .selectByUuid(vehicleUuid)
        .map { it.kind }
        .flatMapLatest { kind ->
            sensorDatabase.selectListByVehicleId(vehicleUuid)
                .map { sensors -> sensors.map { it.location } }
                .map { it.toSet() }
                .map { locations -> kind.locations.subtract(locations).isEmpty() }
        }
        .flowOn(Default)
        .distinctUntilChanged()
}
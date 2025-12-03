package com.masselis.tpmsadvanced.feature.unlocated.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class VehicleBindingStatusUseCase(
    private val vehicleDatabase: VehicleDatabase,
    private val sensorDatabase: SensorDatabase,
) {
    fun boundLocations(vehicleUuid: UUID) = combine(
        vehicleDatabase
            .selectByUuid(vehicleUuid)
            .asFlow()
            .map { it.kind }
            .distinctUntilChanged(),
        sensorDatabase
            .selectListByVehicleId(vehicleUuid)
            .asFlow()
            .distinctUntilChanged()
    ) { kind, sensors -> kind to sensors }
}

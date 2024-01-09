package com.masselis.tpmsadvanced.unlocated.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.UUID

internal class BoundSensorUseCase @AssistedInject constructor(
    private val sensorDatabase: SensorDatabase,
    private val vehicleDatabase: VehicleDatabase,
    @Assisted private val vehicleUuid: UUID,
) {
    @AssistedFactory
    interface Factory : (UUID) -> BoundSensorUseCase

    fun everyWheelIsAlreadyBound() = Pair(
        vehicleDatabase.selectByUuid(vehicleUuid).execute().kind,
        sensorDatabase.selectListByVehicleId(vehicleUuid).execute().map { it.location }.toSet(),
    ).let { (kind, bound) ->
        if (kind.locations.subtract(bound).isEmpty()) kind
        else null
    }
}
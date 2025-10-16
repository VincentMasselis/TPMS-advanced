package com.masselis.tpmsadvanced.feature.unlocated.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import java.util.UUID

@AssistedInject
internal class BoundSensorUseCase(
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

package com.masselis.tpmsadvanced.pecham_binding.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import java.util.UUID
import javax.inject.Inject

internal class BindTyreAndLocationToVehicleUseCase @Inject constructor(
    private val sensorDatabase: SensorDatabase,
) : suspend (UUID, Vehicle.Kind.Location, Tyre) -> Unit {
    override suspend fun invoke(
        vehicleUuid: UUID,
        location: Vehicle.Kind.Location,
        tyre: Tyre
    ) = sensorDatabase.upsert(
        Sensor.Located(
            tyre.id,
            when (location) {
                is Vehicle.Kind.Location.Axle -> SensorLocation.entries.first { it.axle == location.axle }
                is Vehicle.Kind.Location.Side -> SensorLocation.entries.first { it.side == location.side }
                is Vehicle.Kind.Location.Wheel -> location.location
            }
        ),
        vehicleUuid
    )

}
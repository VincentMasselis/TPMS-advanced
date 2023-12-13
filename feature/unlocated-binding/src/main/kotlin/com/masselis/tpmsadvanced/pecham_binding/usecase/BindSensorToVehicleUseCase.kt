package com.masselis.tpmsadvanced.pecham_binding.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import java.util.UUID
import javax.inject.Inject

internal class BindSensorToVehicleUseCase @Inject constructor(
    private val sensorDatabase: SensorDatabase,
    private val tyreDatabase: TyreDatabase,
) {
    suspend fun bind(
        vehicleUuid: UUID,
        sensor: Sensor,
        tyre: Tyre
    ) {
        sensorDatabase.upsert(sensor, vehicleUuid)
        tyreDatabase.insert(
            tyre.run {
                Tyre.Located(
                    timestamp,
                    rssi,
                    id,
                    pressure,
                    temperature,
                    battery,
                    isAlarm,
                    sensor.location
                )
            },
            vehicleUuid
        )
    }
}

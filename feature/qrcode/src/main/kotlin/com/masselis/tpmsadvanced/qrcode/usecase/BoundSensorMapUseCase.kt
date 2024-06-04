package com.masselis.tpmsadvanced.qrcode.usecase

import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.CAR
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.DELTA_THREE_WHEELER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.SINGLE_AXLE_TRAILER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.TADPOLE_THREE_WHEELER
import com.masselis.tpmsadvanced.qrcode.model.QrCodeSensors
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class BoundSensorMapUseCase @Inject constructor(
    private val sensorDatabase: SensorDatabase,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
) {
    suspend fun bind(qrCodeSensors: QrCodeSensors) = coroutineScope {
        val currentUuid = currentVehicleUseCase.value.vehicle.uuid
        val kind = currentVehicleUseCase.value.vehicle.kind
        qrCodeSensors
            .map {
                Sensor(
                    it.id,
                    when (kind) {
                        CAR -> it.wheel

                        SINGLE_AXLE_TRAILER -> it.wheel.toSide()

                        MOTORCYCLE -> it.wheel.toAxle()

                        TADPOLE_THREE_WHEELER -> when (it.wheel.location) {
                            FRONT_LEFT, FRONT_RIGHT -> it.wheel
                            REAR_LEFT, REAR_RIGHT -> it.wheel.toAxle()
                        }

                        DELTA_THREE_WHEELER -> when (it.wheel.location) {
                            FRONT_LEFT, FRONT_RIGHT -> it.wheel.toAxle()
                            REAR_LEFT, REAR_RIGHT -> it.wheel
                        }
                    }
                )
            }
            .map { async { sensorDatabase.upsert(it, currentUuid) } }
            .awaitAll()
    }
}

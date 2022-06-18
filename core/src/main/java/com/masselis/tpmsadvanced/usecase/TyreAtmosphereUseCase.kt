package com.masselis.tpmsadvanced.usecase

import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.TyreAtmosphere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.nio.ByteBuffer
import javax.inject.Inject

class TyreAtmosphereUseCase @Inject constructor(
    private val sensorBytesUseCase: SensorBytesUseCase
) {

    fun listen(): Flow<TyreAtmosphere> = sensorBytesUseCase
        .listen()
        .map { bytes ->
            TyreAtmosphere(
                if (bytes[17].toInt() == 0x00)
                    ByteBuffer.wrap(bytes.copyOfRange(8, 12)).int.let { Pressure(it.div(1000f)) }
                else
                    Pressure(0f),
                ByteBuffer.wrap(bytes.copyOfRange(12, 16)).int.let { Temperature(it.div(100f)) }
            )
        }
}
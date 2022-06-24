package com.masselis.tpmsadvanced.usecase

import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.TyreAtmosphere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class TyreAtmosphereUseCase @Inject constructor(
    private val sensorByteArrayUseCase: SensorByteArrayUseCase
) {

    fun listen(): Flow<TyreAtmosphere> = sensorByteArrayUseCase
        .listen()
        .map { bytes ->
            TyreAtmosphere(
                if (bytes[15].toInt() != PRESSURE_ALERT_BYTE)
                    ByteBuffer.wrap(bytes.copyOfRange(6, 10)).order(ByteOrder.LITTLE_ENDIAN).int.let { Pressure(it.div(1000f)) }
                else
                    Pressure(0f),
                ByteBuffer.wrap(bytes.copyOfRange(10, 14)).order(ByteOrder.LITTLE_ENDIAN).int.let { Temperature(it.div(100f)) }
            )
        }

    companion object {
        private const val PRESSURE_ALERT_BYTE = 0x01
    }
}
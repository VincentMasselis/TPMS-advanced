package com.masselis.tpmsadvanced.core.usecase

import com.masselis.tpmsadvanced.core.model.Pressure
import com.masselis.tpmsadvanced.core.model.Temperature
import com.masselis.tpmsadvanced.core.model.TyreAtmosphere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class TyreAtmosphereUseCase @Inject constructor(
    private val recordUseCaseImpl: RecordUseCase
) {

    fun listen(): Flow<TyreAtmosphere> = recordUseCaseImpl
        .listen()
        .map { record ->
            TyreAtmosphere(
                record.timestamp,
                if (record.alarm() != PRESSURE_ALARM_BYTE)
                    ByteBuffer
                        .wrap(record.pressure())
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .int
                        .let { Pressure(it.div(1000f)) }
                else
                    Pressure(0f),
                ByteBuffer
                    .wrap(record.temperature())
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .int
                    .let { Temperature(it.div(100f)) }
            )
        }

    companion object {
        private const val PRESSURE_ALARM_BYTE = 0x01.toByte()
    }
}
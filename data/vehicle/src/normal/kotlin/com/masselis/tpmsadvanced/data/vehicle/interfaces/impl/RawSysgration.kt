package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalUnsignedTypes::class)
@JvmInline
@Suppress("MagicNumber")
internal value class RawSysgration private constructor(
    private val manufacturerData: ByteArray
) : Raw {

    fun location() = manufacturerData[0]
        .toUByte()
        .let { raw -> SensorLocation.entries.first { it.byte == raw } }

    fun address() = manufacturerData.copyOfRange(1, 3)

    fun id() = ByteBuffer.wrap(byteArrayOf(0x00) + manufacturerData.copyOfRange(3, 6))
        .order(ByteOrder.LITTLE_ENDIAN)
        .int

    fun pressure() = ByteBuffer.wrap(manufacturerData.copyOfRange(6, 10))
        .order(ByteOrder.LITTLE_ENDIAN)
        .int
        .div(1000f)
        .kpa

    fun temperature() = ByteBuffer.wrap(manufacturerData.copyOfRange(10, 14))
        .order(ByteOrder.LITTLE_ENDIAN)
        .int
        .div(100f)
        .celsius

    fun battery() = manufacturerData[14].toInt().toUShort()

    fun isAlarm() = manufacturerData[15] == PRESSURE_ALARM_BYTE

    override fun asTyre() =
        Tyre(now(), location(), id(), pressure(), temperature(), battery(), isAlarm())

    companion object {
        private const val PRESSURE_ALARM_BYTE = 0x01.toByte()
        private val expectedAddress = ubyteArrayOf(0xEAu, 0xCAu).toByteArray()

        operator fun invoke(manufacturerData: ByteArray): RawSysgration? =
            RawSysgration(manufacturerData)
                .takeIf { it.address().contentEquals(expectedAddress) }
    }
}
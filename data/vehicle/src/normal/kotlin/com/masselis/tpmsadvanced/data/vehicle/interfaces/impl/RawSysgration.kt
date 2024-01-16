package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalUnsignedTypes::class)
@Suppress("MagicNumber", "DataClassPrivateConstructor")
internal data class RawSysgration private constructor(
    private val rssi: Int,
    private val manufacturerData: ByteArray
) : Raw {

    fun location() = manufacturerData[0]
        .toUByte()
        .let { raw -> SensorLocation.entries.first { it.byte == raw } }

    fun address() = manufacturerData.copyOfRange(1, 3)

    fun id() = ByteBuffer
        .wrap(byteArrayOf(0x00) + manufacturerData.copyOfRange(3, 6))
        .order(ByteOrder.LITTLE_ENDIAN)
        .int

    fun pressure() = ByteBuffer
        .wrap(manufacturerData.copyOfRange(6, 10))
        .order(ByteOrder.LITTLE_ENDIAN)
        .int
        .div(1000f)
        .kpa

    fun temperature() = ByteBuffer
        .wrap(manufacturerData.copyOfRange(10, 14))
        .order(ByteOrder.LITTLE_ENDIAN)
        .int
        .div(100f)
        .celsius

    fun battery() = manufacturerData[14].toInt().toUShort()

    fun isAlarm() = manufacturerData[15] == PRESSURE_ALARM_BYTE

    override fun asTyre() = Tyre.SensorLocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        battery(),
        isAlarm(),
        location()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawSysgration

        if (rssi != other.rssi) return false
        return manufacturerData.contentEquals(other.manufacturerData)
    }

    override fun hashCode(): Int {
        var result = rssi
        result = 31 * result + manufacturerData.contentHashCode()
        return result
    }

    companion object {
        private const val PRESSURE_ALARM_BYTE = 0x01.toByte()
        private val expectedAddress = ubyteArrayOf(0xEAu, 0xCAu).toByteArray()

        operator fun invoke(scanResult: ScanResult, manufacturerData: ByteArray): RawSysgration? =
            RawSysgration(scanResult.rssi, manufacturerData)
                .takeIf { it.address().contentEquals(expectedAddress) }
    }
}
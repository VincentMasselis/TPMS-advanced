package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import androidx.core.util.size
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID.fromString

@OptIn(ExperimentalUnsignedTypes::class)
@Suppress("MagicNumber")
@ConsistentCopyVisibility
internal data class RawSysgration private constructor(
    private val rssi: Int,
    private val manufacturerData: ByteArray
) : Raw {

    fun location() = manufacturerData[0]
        .toUByte()
        .let { byte ->
            when (byte.toUInt()) {
                0x80u -> Location.Wheel.FRONT_LEFT
                0x81u -> Location.Wheel.FRONT_RIGHT
                0x82u -> Location.Wheel.REAR_LEFT
                0x83u -> Location.Wheel.REAR_RIGHT
                else -> error("Unknown location byte: \"$byte\"")
            }
        }

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

    override fun asTyre() = Tyre.Located(
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
        internal val SERVICE_UUID = ParcelUuid(fromString("0000fbb0-0000-1000-8000-00805f9b34fb"))
        private const val PRESSURE_ALARM_BYTE = 0x01.toByte()
        private val expectedAddress = ubyteArrayOf(0xEAu, 0xCAu).toByteArray()

        operator fun invoke(scanResult: ScanResult): RawSysgration? = scanResult
            .scanRecord
            ?.manufacturerSpecificData
            ?.takeIf { it.size > 0 }
            ?.valueAt(0)
            .let { it ?: return null }
            .let { RawSysgration(scanResult.rssi, it) }
            .takeIf { it.address().contentEquals(expectedAddress) }
    }
}

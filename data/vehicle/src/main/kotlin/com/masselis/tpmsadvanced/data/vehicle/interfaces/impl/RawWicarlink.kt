package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.util.UUID.fromString
import kotlin.math.roundToInt

@ConsistentCopyVisibility
@Suppress("MagicNumber")
internal data class RawWicarlink private constructor(
    private val rssi: Int,
    private val data: ByteArray,
) : Raw {

    fun id() = (data[23].toInt() and 0xFF) or
            ((data[24].toInt() and 0xFF) shl 8) or
            ((data[25].toInt() and 0xFF) shl 16)

    fun pressure() = (data[12].toInt() and 0xFF)
        .let { raw -> if ((data[17].toInt() and 0xFF) == 1) raw + 256 else raw }
        .times(3.144f)
        .kpa

    fun voltage() = (data[11].toInt() and 0xFF) * 0.01f + 1.22f // Returns 2.7 for 2.7 Volts

    fun temperature() = ((data[13].toInt() and 0xFF) - 55).toFloat().celsius

    override fun asTyre(): Tyre.SensorInput = Tyre.Unlocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        voltage().times(10f).roundToInt().toUShort(),
        voltage() <= 2.1, // Mimic the LYTPMS app
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawWicarlink

        if (rssi != other.rssi) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rssi
        result = 31 * result + data.contentHashCode()
        return result
    }

    companion object {
        internal val SERVICE_UUID = ParcelUuid(fromString("0000fbb0-0000-1000-8000-00805f9b34fb"))
        @Suppress("ReturnCount")
        operator fun invoke(scanResult: ScanResult): RawWicarlink? {
            val scanRecord = scanResult.scanRecord ?: return null
            if (scanRecord.advertiseFlags != 0x06) return null
            if (scanRecord.serviceUuids?.contains(SERVICE_UUID)?.not() ?: true) return null
            if (CRC.validate(scanRecord.bytes).not()) return null

            return RawWicarlink(scanResult.rssi, scanRecord.bytes)
        }

        // Reversed engineered from the official LYTPMS app with the help of Claude
        private object CRC {
            fun validate(bytes: ByteArray): Boolean {
                val buf = ByteArray(14)
                bytes.copyInto(buf, destinationOffset = 0, startIndex = 9, endIndex = 16)
                bytes.copyInto(buf, destinationOffset = 7, startIndex = 19, endIndex = 26)
                val expected = (((bytes[16].toInt() and 0xFF) shl 8) or (bytes[18].toInt() and 0xFF)) - bytes[17]
                return crc16XMODEM(buf) == expected
            }

            private fun crc16XMODEM(bytes: ByteArray): Int {
                var crc = 0
                for (byte in bytes) {
                    crc = crc xor ((byte.toInt() and 0xFF) shl 8)
                    repeat(8) {
                        crc = if (crc and 0x8000 != 0) (crc shl 1) xor 0x1021 else crc shl 1
                    }
                }
                return crc and 0xFFFF
            }
        }
    }
}

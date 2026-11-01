package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import co.touchlab.kermit.Logger
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.util.UUID.fromString

/**
 * Sensor sold with the DJTPMS app.
 *
 * The advertisement is a legacy one, made of the following AD structures:
 * ```
 * 02 01 06                             Flags
 * 03 02 E0 FF                          Incomplete list of 16-bit UUIDs (0xFFE0)
 * 07 08 44 4A 54 50 4D 53              Shortened local name "DJTPMS"
 * 0F FF <14 bytes>                     Manufacturer data
 * ```
 * The manufacturer data always starts with 2 "company id"-ish bytes (00 00, 00 0C...), that are
 * ignored, followed by:
 * ```
 * index  meaning
 *   2    battery * 10         (0x1D => 2.9 volts)
 *   3    temperature, int8    (0x17 => 23°C, negative values below 0°C)
 *   4-5  absolute pressure, uint16 BE  (0x0136 => 310 kPa absolute)
 *   6    flags, always 0x00 so far
 *   7    CRC8 (poly 0x2F, init 0xDF, MSB first, no reflection, no final xor)
 *        over [CID0, CID1, battery, temperature, pressure high, pressure low]
 *   8-13 sensor MAC address, also broadcasted by the device itself (frames are sometimes as
 *        short as 12 bytes, without the MAC, so it is only used when present)
 * ```
 * The pressure broadcasted by the sensor is an **absolute** one, it has to be turned into a
 * gauge pressure (`absolute - 101.3 kPa`) to be comparable with the other sensors.
 */
@ConsistentCopyVisibility
@Suppress("MagicNumber", "MaxLineLength")
internal data class RawDjtpms private constructor(
    private val rssi: Int,
    /** Manufacturer data of the 0xFF AD structure, company id bytes included. */
    private val manufacturerData: ByteArray,
    /**
     * Stable identifier of the sensor. Built from the MAC address embedded in the payload when
     * available, otherwise derived from [ScanResult.getDevice().getAddress], which Android can
     * randomize.
     */
    private val sensorId: Int
) : Raw {

    fun battery() = manufacturerData[2].toUShort() // Returns 29 for 2.9 volts

    fun temperature() = manufacturerData[3].toFloat().celsius

    fun pressure() = absolutePressure()
        .minus(ATMOSPHERIC_PRESSURE_KPA)
        .toFloat()
        .kpa

    private fun absolutePressure() = ((manufacturerData[4].toInt() and 0xFF) shl 8) or
            (manufacturerData[5].toInt() and 0xFF)

    // CRC8 over [CID0, CID1, battery, temperature, pressure high, pressure low], MSB first,
    // no reflection, no final xor.
    private fun hasValidCrc(): Boolean {
        var value = CRC8_INIT
        for (i in CRC_INPUT_INDEXES) {
            value = value xor (manufacturerData[i].toInt() and 0xFF)
            repeat(8) {
                value = if (value and 0x80 != 0)
                    ((value shl 1) and 0xFF) xor CRC8_POLY
                else
                    (value shl 1) and 0xFF
            }
        }
        return value == (manufacturerData[7].toInt() and 0xFF)
    }

    fun id() = sensorId

    // DJTPMS does not broadcast the wheel the sensor is mounted on, sensors are bound manually.
    override fun asTyre(): ScannerRecord = Tyre.Unlocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        battery(),
        false // No alarm flag is broadcasted
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawDjtpms

        // `rssi` is deliberately excluded: it varies on every advertisement while the payload
        // stays the same, and including it would defeat the `distinctUntilChanged()` of the
        // scanner, flooding the UI with duplicates of the same measurement.
        if (sensorId != other.sensorId) return false
        return manufacturerData.contentEquals(other.manufacturerData)
    }

    override fun hashCode(): Int {
        var result = sensorId
        result = 31 * result + manufacturerData.contentHashCode()
        return result
    }

    companion object {
        internal const val DEVICE_NAME = "DJTPMS"
        private const val ATMOSPHERIC_PRESSURE_KPA = 101
        private val CRC_INPUT_INDEXES = intArrayOf(0, 1, 2, 3, 4, 5)
        // 12 bytes are enough to decode everything: the last 2 (part of the MAC) can be missing.
        private const val MIN_MANUFACTURER_DATA_SIZE = 12
        private const val MAC_INCLUDED_MANUFACTURER_DATA_SIZE = 14
        private const val AD_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF
        private val logger = Logger.withTag("RawDjtpms.Companion")
        private const val CRC8_POLY = 0x2F
        private const val CRC8_INIT = 0xDF
        internal val SERVICE_UUID = ParcelUuid(fromString("0000ffe0-0000-1000-8000-00805f9b34fb"))

        @Suppress("ReturnCount")
        operator fun invoke(result: ScanResult): RawDjtpms? {
            val scanRecord = result.scanRecord
                ?: return null
            if (scanRecord.deviceName != DEVICE_NAME)
                return null
            // The UUID is broadcasted inside an *incomplete* 16-bit list, and most Android
            // versions only expose it when it is present, so a missing list is not a rejection.
            // A list that is present but holds another UUID is.
            scanRecord.serviceUuids?.let { uuids ->
                if (SERVICE_UUID !in uuids) return null
            }
            val manufacturerData = runCatching { extractManufacturerData(scanRecord) }
                .onFailure { logger.e("Filled bytes are incorrect: ${scanRecord.bytes.toHexString()}", it) }
                .getOrNull()
                ?: return null
            if (manufacturerData.size < MIN_MANUFACTURER_DATA_SIZE)
                return null
            val raw = RawDjtpms(
                rssi = result.rssi,
                manufacturerData = manufacturerData,
                sensorId = manufacturerData.sensorId(result)
            )
            if (!raw.hasValidCrc()) {
                logger.w("Invalid CRC for DJTPMS frame: ${manufacturerData.toHexString()}")
                return null
            }
            return raw
        }

        // Takes the last 4 bytes of the MAC address when the payload carries it: it is stable for
        // a given sensor, unlike `ScanResult.device.address` that Android may randomize.
        private fun ByteArray.sensorId(result: ScanResult): Int =
            if (size >= MAC_INCLUDED_MANUFACTURER_DATA_SIZE)
                ((this[10].toInt() and 0xFF) shl 24) or
                        ((this[11].toInt() and 0xFF) shl 16) or
                        ((this[12].toInt() and 0xFF) shl 8) or
                        (this[13].toInt() and 0xFF)
            else
                result.device?.address?.hashCode() ?: 0

        /**
         * Walks the AD structures instead of relying on
         * `scanRecord.manufacturerSpecificData.valueAt(0)`, which can be truncated depending on
         * the Android version.
         */
        private fun extractManufacturerData(scanRecord: ScanRecord): ByteArray? {
            val bytes = scanRecord.bytes
            var index = 0
            while (index + 1 < bytes.size) {
                val length = bytes[index].toInt() and 0xFF
                if (length == 0) return null
                val type = bytes[index + 1].toInt() and 0xFF
                if (type == AD_TYPE_MANUFACTURER_SPECIFIC_DATA)
                    return bytes.copyOfRange(index + 2, minOf(index + 1 + length, bytes.size))
                index += length + 1
            }
            return null
        }
    }
}

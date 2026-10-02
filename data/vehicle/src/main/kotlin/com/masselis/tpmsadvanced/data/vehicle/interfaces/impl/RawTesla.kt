package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import kotlin.math.roundToInt

/**
 * Tesla BLE TPMS sensor (Model 3/Y/S/X OEM sensor and Tesla-compatible clones like the Autel
 * MX-Sensor BLE). Only the advertisement is decoded here, the GATT connection used by the car is
 * not.
 *
 * Manufacturer data payload, company ID `0x022B` (Tesla, Inc.) already stripped:
 * ```
 * offset  size  field
 * 0       1     unknown, `0x01` on every capture
 * 1       1     unknown, `0xFE` on every sleep frame
 * 2       1     status, below `0x05` the sensor is asleep and the next fields are stale or missing
 * 3       2     pressure, uint16 little-endian, absolute kPa
 * 5       1     temperature, uint8, °C + 50
 * 6       2     battery, uint16 little-endian, mV
 * ```
 * Reverse engineered by [cunzulatu/Tesla_BLE_TPMS](https://github.com/cunzulatu/Tesla_BLE_TPMS) and
 * [anufryieu/tesla-tpms-ble](https://github.com/anufryieu/tesla-tpms-ble/blob/main/docs/PROTOCOL.md).
 * The pressure and temperature formulas are inferred, they still have to be checked against a
 * reference gauge.
 *
 * While the car is driving, it holds a GATT connection with each sensor which stops it from
 * advertising, so this decoder mostly receives data when the car isn't connected to the sensor.
 */
@ConsistentCopyVisibility
@Suppress("MagicNumber")
internal data class RawTesla private constructor(
    private val macAddress: String,
    private val rssi: Int,
    private val manufacturerData: ByteArray,
) : Raw {

    // The payload doesn't contain any sensor ID, bytes 0 and 1 are identical across sensors
    fun id() = macAddress.hashCode()

    fun status() = manufacturerData[2].toInt() and 0xFF

    // The sensor measures an absolute pressure, removing 100 kPa (~1 atm) returns the gauge
    // pressure. Clamped because the atmospheric pressure can be slightly below 100 kPa.
    fun pressure() = ((manufacturerData[3].toInt() and 0xFF) or ((manufacturerData[4].toInt() and 0xFF) shl 8))
        .minus(100)
        .coerceAtLeast(0)
        .toFloat()
        .kpa

    fun temperature() = (manufacturerData[5].toInt() and 0xFF)
        .minus(50)
        .toFloat()
        .celsius

    fun batteryMv() = (manufacturerData[6].toInt() and 0xFF) or ((manufacturerData[7].toInt() and 0xFF) shl 8)

    override fun asTyre(): ScannerRecord = Tyre.Unlocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        batteryMv().div(100f).roundToInt().toUShort(), // Returns 30 for 3.0 volts
        false, // Doesn't exist in the context of a Tesla BLE sensor
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawTesla

        if (macAddress != other.macAddress) return false
        if (rssi != other.rssi) return false
        return manufacturerData.contentEquals(other.manufacturerData)
    }

    override fun hashCode(): Int {
        var result = macAddress.hashCode()
        result = 31 * result + rssi
        result = 31 * result + manufacturerData.contentHashCode()
        return result
    }

    companion object {
        internal const val MANUFACTURER_ID = 0x022B

        private const val MIN_MANUFACTURER_DATA_LENGTH = 8
        private const val MIN_AWAKE_STATUS = 0x05

        // Covers every lithium cell used in TPMS sensors
        private val PLAUSIBLE_BATTERY_MV = 1500..4300

        operator fun invoke(scanResult: ScanResult): RawTesla? = scanResult
            .scanRecord
            ?.getManufacturerSpecificData(MANUFACTURER_ID)
            // A sleeping sensor only sends the first 3 bytes (`01 FE 03`), there is nothing to read
            ?.takeIf { it.size >= MIN_MANUFACTURER_DATA_LENGTH }
            ?.let { RawTesla(scanResult.device.address, scanResult.rssi, it) }
            ?.takeIf { it.status() >= MIN_AWAKE_STATUS }
            // Tesla vehicles also advertise with the Tesla company ID, their payload is filtered
            // out by checking the battery voltage is plausible.
            ?.takeIf { it.batteryMv() in PLAUSIBLE_BATTERY_MV }
    }
}

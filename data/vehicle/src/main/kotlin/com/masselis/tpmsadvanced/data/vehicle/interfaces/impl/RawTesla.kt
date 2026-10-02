package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.RawTesla.Companion.MANUFACTURER_ID


/**
 * Tesla BLE TPMS sensor (Model 3/Y/S/X OEM sensor and Tesla-compatible clones like the Autel
 * MX-Sensor BLE). Only the advertisement is decoded, the GATT connection used by the car is not.
 *
 * Manufacturer data payload, company ID [MANUFACTURER_ID] (Tesla, Inc.) already stripped:
 * ```
 * offset  size  field
 * 0       1     unknown, `0x01` on every capture
 * 1       1     unknown, `0xFE` on every sleep frame
 * 2       1     status, see [Status]
 * 3       2     pressure, uint16 little-endian, absolute kPa
 * 5       1     temperature, uint8, °C + 50
 * 6       2     battery, uint16 little-endian, mV
 * ```
 * A sleep frame usually stops after the status byte (`01 FE 03`), see [RawTeslaSleep]. An awake
 * frame contains every field, see [RawTeslaAwake].
 *
 * Reverse engineered by [cunzulatu/Tesla_BLE_TPMS](https://github.com/cunzulatu/Tesla_BLE_TPMS) and
 * [anufryieu/tesla-tpms-ble](https://github.com/anufryieu/tesla-tpms-ble/blob/main/docs/PROTOCOL.md).
 *
 * The payload doesn't contain any sensor ID, bytes 0 and 1 are identical across sensors. The MAC
 * address is a public Texas Instruments address (`BC:6A:29`) which never changes, the car itself
 * identifies its sensors with it, so [id] is derived from it and is shared by sleep and awake frames.
 */
@Suppress("MagicNumber")
internal sealed interface RawTesla : Raw {
    /**
     * Only the `0x05` threshold is known, it comes from the
     * [ESP32 sketch](https://github.com/cunzulatu/Tesla_BLE_TPMS/blob/4ac6018ea29f22b34c3850a4056529d32e2cba56/Tesla_BLE_TPMS_v03.ino#L146).
     * The exact values are not published, `0x03` is the only one captured so far (sleep frame).
     */
    @Suppress("MaxLineLength")
    enum class Status(val intRange: IntRange) {
        SLEEP(0x00 until 0x05),
        AWAKE(0x05..0xFF);
    }

    val macAddress: String
    val manufacturerData: ByteArray

    fun id() = macAddress.hashCode()

    fun status(): Status = (manufacturerData[2].toInt() and 0xFF)
        .let { rawStatus -> Status.entries.first { rawStatus in it.intRange } }

    companion object {
        internal const val MANUFACTURER_ID = 0x022B
    }
}

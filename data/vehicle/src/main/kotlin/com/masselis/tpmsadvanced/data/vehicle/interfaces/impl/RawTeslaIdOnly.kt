package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.RawTeslaIdOnly.Companion.MANUFACTURER_ID
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord

/**
 * Tesla BLE TPMS sensor (Model 3/Y/S/X OEM sensor and Tesla-compatible clones like the Autel
 * MX-Sensor BLE). Only the advertisement is decoded, the GATT connection used by the car is not.
 *
 * Manufacturer data payload, company ID [MANUFACTURER_ID] (Tesla, Inc.) already stripped:
 * ```
 * offset  size  field
 * 0       1     unknown, `0x01` on every capture
 * 1       1     unknown, `0xFE` on every capture
 * 2       1     status, see [Status]
 * ```
 * Only sleep frames (`01 FE 03`) have been captured, roughly once a second while the wheel doesn't
 * turn, even after driving. They don't contain any measurement but they allow the sensor to be found
 * while the car is parked. The car reads the measurements through a GATT connection instead.
 *
 * Reverse engineered by [cunzulatu/Tesla_BLE_TPMS](https://github.com/cunzulatu/Tesla_BLE_TPMS) and
 * [anufryieu/tesla-tpms-ble](https://github.com/anufryieu/tesla-tpms-ble/blob/main/docs/PROTOCOL.md).
 *
 * The payload doesn't contain any sensor ID, bytes 0 and 1 are identical across sensors. The MAC
 * address is a public Texas Instruments address (`BC:6A:29`) which never changes, the car itself
 * identifies its sensors with it, so [id] is derived from it.
 */
@ConsistentCopyVisibility
@Suppress("MagicNumber")
internal data class RawTeslaIdOnly private constructor(
    private val macAddress: String,
    private val rssi: Int,
    private val manufacturerData: ByteArray,
) : Raw {

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

    fun id() = macAddress.hashCode()

    fun status(): Status = (manufacturerData[2].toInt() and 0xFF)
        .let { rawStatus -> Status.entries.first { rawStatus in it.intRange } }

    override fun asTyre(): ScannerRecord = ScannerRecord.Impl(now(), rssi, id())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawTeslaIdOnly

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

        // Captured sleep frames are 3 bytes long (`01 FE 03`), some sensors could also keep sending
        // stale measurements which would make them up to 8 bytes long. Anything longer is not a TPMS
        // sensor, Tesla vehicles also advertise with the Tesla company ID.
        private val SLEEP_FRAME_LENGTH = 3..8

        operator fun invoke(scanResult: ScanResult): RawTeslaIdOnly? = scanResult
            .scanRecord
            ?.getManufacturerSpecificData(MANUFACTURER_ID)
            ?.takeIf { it.size in SLEEP_FRAME_LENGTH }
            ?.let { RawTeslaIdOnly(scanResult.device.address, scanResult.rssi, it) }
            ?.takeIf { it.status() == Status.SLEEP }
    }
}

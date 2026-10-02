package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord

/**
 * Sleep frame of a Tesla BLE TPMS sensor, see [RawTesla] for the payload layout.
 *
 * A sensor sends a sleep frame roughly once a second while the wheel doesn't turn. It doesn't
 * contain any measurement but it allows the sensor to be found while the car is parked. Since
 * [RawTesla.id] is shared with [RawTeslaAwake], a sensor found through a sleep frame is recognized
 * when it sends an awake frame.
 */
@ConsistentCopyVisibility
@Suppress("MagicNumber")
internal data class RawTeslaSleep private constructor(
    override val macAddress: String,
    private val rssi: Int,
    override val manufacturerData: ByteArray,
) : RawTesla {

    override fun asTyre(): ScannerRecord = ScannerRecord.Impl(now(), rssi, id())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawTeslaSleep

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
        // Captured sleep frames are 3 bytes long (`01 FE 03`), some sensors could also keep sending
        // their stale measurements which makes them 8 bytes long like an awake frame. Anything
        // longer is not a TPMS sensor, Tesla vehicles also advertise with the Tesla company ID.
        private val SLEEP_FRAME_LENGTH = 3..8

        operator fun invoke(scanResult: ScanResult): RawTeslaSleep? = scanResult
            .scanRecord
            ?.getManufacturerSpecificData(RawTesla.MANUFACTURER_ID)
            ?.takeIf { it.size in SLEEP_FRAME_LENGTH }
            ?.let { RawTeslaSleep(scanResult.device.address, scanResult.rssi, it) }
            ?.takeIf { it.status() == RawTesla.Status.SLEEP }
    }
}

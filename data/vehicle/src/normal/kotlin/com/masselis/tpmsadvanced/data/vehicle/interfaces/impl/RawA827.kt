package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.util.UUID.fromString
import kotlin.math.roundToInt

@Suppress("MagicNumber")
internal data class RawA827 private constructor(
    private val rssi: Int,
    private val manufacturerData: ByteArray,
) : Raw {

    fun id() =
        (manufacturerData[4].toInt() and 0xFF) or
                ((manufacturerData[5].toInt() and 0xFF) shl 8) or
                ((manufacturerData[6].toInt() and 0xFF) shl 16)

    fun pressure() = (
            ((manufacturerData[2].toInt() and 0xFF) shl 8) or
                    (manufacturerData[3].toInt() and 0xFF)
            )
        .minus(100)
        .toFloat()
        .kpa

    fun temperature() =
        ((manufacturerData[1].toInt() and 0xFF) - 50)
            .toFloat()
            .celsius

    fun voltage() =
        (manufacturerData[0].toInt() and 0xFF) * 0.01f + 1.22f

    fun battery() =
        voltage()
            .times(10f)
            .roundToInt()
            .toUShort()

    override fun asTyre(): Tyre.SensorInput = Tyre.Unlocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        battery(),
        false,
    )

    companion object {
        internal val SERVICE_UUID = ParcelUuid(
            fromString("0000a827-0000-1000-8000-00805f9b34fb")
        )

        private const val MANUFACTURER_ID = 0x0002
        private const val MIN_MANUFACTURER_DATA_LENGTH = 9

        operator fun invoke(scanResult: ScanResult): RawA827? {
            val scanRecord = scanResult.scanRecord ?: return null

            if (scanRecord.serviceUuids?.contains(SERVICE_UUID)?.not() ?: true) {
                return null
            }

            val manufacturerData =
                scanRecord.getManufacturerSpecificData(MANUFACTURER_ID)
                    ?: return null

            if (manufacturerData.size < MIN_MANUFACTURER_DATA_LENGTH) {
                return null
            }

            return RawA827(scanResult.rssi, manufacturerData)
        }
    }
}

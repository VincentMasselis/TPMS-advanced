package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.util.UUID.fromString

@Suppress("MagicNumber")
internal data class RawA827 private constructor(
    private val rssi: Int,
    private val data: ByteArray,
) : Raw {

    fun id() =
        (data[14].toInt() and 0xFF) or
                ((data[15].toInt() and 0xFF) shl 8) or
                ((data[16].toInt() and 0xFF) shl 16)

    fun pressure() = (
            ((data[12].toInt() and 0xFF) shl 8) or
                    (data[13].toInt() and 0xFF)
            )
        .minus(100)
        .toFloat()
        .kpa

    fun temperature() =
        ((data[11].toInt() and 0xFF) - 50)
            .toFloat()
            .celsius

    override fun asTyre(): Tyre.SensorInput = Tyre.Unlocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        100u,
        false,
    )

    companion object {
        internal val SERVICE_UUID = ParcelUuid(
            fromString("0000a827-0000-1000-8000-00805f9b34fb")
        )

        operator fun invoke(scanResult: ScanResult): RawA827? {
            val scanRecord = scanResult.scanRecord ?: return null

            if (scanRecord.serviceUuids?.contains(SERVICE_UUID)?.not() ?: true) {
                return null
            }

            val bytes = scanRecord.bytes

            if (bytes.size < 17) {
                return null
            }

            return RawA827(scanResult.rssi, bytes)
        }
    }
}
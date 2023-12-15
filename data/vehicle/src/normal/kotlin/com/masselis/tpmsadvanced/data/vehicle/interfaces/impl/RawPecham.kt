package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.le.ScanResult
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalStdlibApi::class)
@Suppress("DataClassPrivateConstructor")
internal data class RawPecham private constructor(
    private val macAddress: String,
    private val rssi: Int,
    private val data: ByteArray
) : Raw {

    fun id() = macAddress.hashCode()
    fun pressure() = data
        .copyOfRange(3, 5)
        .let {
            ByteBuffer.wrap(it)
                .order(ByteOrder.BIG_ENDIAN)
                .getShort()
        }
        .div(10f)
        .psi

    fun battery() = data[1].toUShort() // Returns 27 for 2.7 volts

    fun temperature() = data[2].toFloat().celsius

    override fun asTyre() = Tyre.Unlocated(
        now(),
        rssi,
        id(),
        pressure(),
        temperature(),
        battery(),
        battery() < 26u // Mimics the alarm from the official app
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawPecham

        if (macAddress != other.macAddress) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = macAddress.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    companion object {
        operator fun invoke(result: ScanResult): RawPecham? {
            if (result.scanRecord?.deviceName != "BR")
                return null
            // Calling result.scanRecord?.manufacturerSpecificData?.valueAt(0) will not work because
            // the returned array is 5 bytes only instead of 7 bytes. It doesn't contain the first 2
            // bytes
            val data = runCatching { result.scanRecord?.bytes?.copyOfRange(10, 17) }
                .onFailure {
                    Firebase.crashlytics.recordException(
                        IllegalArgumentException(
                            "Even if the device is named \"BR\" filled bytes are incorrect: $${result.scanRecord?.bytes?.toHexString()}",
                            it
                        )
                    )
                }
                .getOrNull()
                ?: return null
            return RawPecham(result.device.address, result.rssi, data)
        }
    }
}
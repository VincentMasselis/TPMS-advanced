package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.psi
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Suppress("DataClassPrivateConstructor")
internal data class RawPecham private constructor(
    private val macAddress: String,
    private val rssi: Int,
    private val manufacturerData: ByteArray
) : Raw {
    fun id() = macAddress.hashCode()
    fun pressure() = manufacturerData
        .copyOfRange(3, 5)
        .let {
            ByteBuffer.wrap(it)
                .order(ByteOrder.BIG_ENDIAN)
                .getShort()
        }
        .div(10f)
        .psi

    fun battery() = manufacturerData[1].toUShort() // Returns 27 for 2.7 volts

    fun temperature() = manufacturerData[2].toFloat().celsius

    override fun asTyre() = Tyre(
        now(),
        rssi,
        null,
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
        return manufacturerData.contentEquals(other.manufacturerData)
    }

    override fun hashCode(): Int {
        var result = macAddress.hashCode()
        result = 31 * result + manufacturerData.contentHashCode()
        return result
    }

    companion object {
        operator fun invoke(result: ScanResult, manufacturerData: ByteArray): RawPecham? {
            if (result.scanRecord!!.deviceName != "BR")
                return null
            return RawPecham(result.device.address, result.rssi, manufacturerData)
        }
    }
}
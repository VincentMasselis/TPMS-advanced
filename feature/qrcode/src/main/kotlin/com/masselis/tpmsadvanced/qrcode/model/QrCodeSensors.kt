package com.masselis.tpmsadvanced.qrcode.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class QrCodeSensor(
    val id: Int,
    val wheel: Vehicle.Kind.Location.Wheel
) : Parcelable

@Suppress("MagicNumber")
internal sealed interface QrCodeSensors : Parcelable {

    fun toSet(): Set<QrCodeSensor>

    @Parcelize
    data class FourWheel(
        val first: QrCodeSensor,
        val second: QrCodeSensor,
        val third: QrCodeSensor,
        val fourth: QrCodeSensor,
    ) : QrCodeSensors {
        init {
            val list = toSet()
            require(list.distinctBy { it.wheel }.size == 4)
            require(list.distinctBy { it.id }.size == 4)
        }

        override fun toSet(): Set<QrCodeSensor> = setOf(first, second, third, fourth)
    }

    @Parcelize
    data class TwoWheel(
        val first: QrCodeSensor,
        val second: QrCodeSensor,
    ) : QrCodeSensors {
        init {
            val list = toSet()
            require(list.distinctBy { it.wheel }.size == 2)
            require(list.distinctBy { it.id }.size == 2)
        }

        override fun toSet(): Set<QrCodeSensor> = setOf(first, second)
    }
}

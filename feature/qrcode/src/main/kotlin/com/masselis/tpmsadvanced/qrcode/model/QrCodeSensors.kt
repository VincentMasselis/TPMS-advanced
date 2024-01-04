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
internal sealed interface QrCodeSensors : Set<QrCodeSensor>, Parcelable {

    @Parcelize
    @JvmInline
    value class FourWheel private constructor(
        private val set: Set<QrCodeSensor>
    ) : QrCodeSensors, Set<QrCodeSensor> by set {
        constructor(
            first: QrCodeSensor,
            second: QrCodeSensor,
            third: QrCodeSensor,
            fourth: QrCodeSensor
        ) : this(setOf(first, second, third, fourth)) {
            require(distinctBy { it.wheel }.size == 4)
            require(distinctBy { it.id }.size == 4)
        }
    }

    @Parcelize
    @JvmInline
    value class TwoWheel private constructor(
        private val set: Set<QrCodeSensor>
    ) : QrCodeSensors, Set<QrCodeSensor> by set {
        constructor(first: QrCodeSensor, second: QrCodeSensor) : this(setOf(first, second)) {
            require(distinctBy { it.wheel }.size == 2)
            require(distinctBy { it.id }.size == 2)
        }
    }
}

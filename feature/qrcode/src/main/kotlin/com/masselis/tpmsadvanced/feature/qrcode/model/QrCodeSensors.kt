package com.masselis.tpmsadvanced.feature.qrcode.model

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
            require(distinctBy { it.wheel }.size == 4) { throw DuplicateWheelLocation(map { it.wheel }) }
            require(distinctBy { it.id }.size == 4) { throw DuplicateId(map { it.id }) }
        }
    }

    @Parcelize
    @JvmInline
    value class TwoWheel private constructor(
        private val set: Set<QrCodeSensor>
    ) : QrCodeSensors, Set<QrCodeSensor> by set {
        constructor(first: QrCodeSensor, second: QrCodeSensor) : this(setOf(first, second)) {
            require(distinctBy { it.wheel }.size == 2) { throw DuplicateWheelLocation(map { it.wheel }) }
            require(distinctBy { it.id }.size == 2) { throw DuplicateId(map { it.id }) }
        }
    }

    data class DuplicateWheelLocation(
        val wheels: Collection<Vehicle.Kind.Location.Wheel>
    ) : IllegalArgumentException()

    data class DuplicateId(
        val ids: Collection<Int>
    ) : IllegalArgumentException()
}

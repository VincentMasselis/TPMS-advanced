package com.masselis.tpmsadvanced.core.model

import android.os.Parcel
import android.os.Parcelable
import com.masselis.tpmsadvanced.unit.model.PressureUnit
import com.masselis.tpmsadvanced.unit.model.PressureUnit.*

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@JvmInline
value class Pressure(val kpa: Float) : Parcelable {

    fun asBar() = convert(BAR)

    fun asPsi() = convert(PSI)

    fun convert(unit: PressureUnit) = when (unit) {
        KILO_PASCAL -> kpa
        BAR -> kpa / 100f
        PSI -> kpa / 6.895f
    }

    fun string(unit: PressureUnit) = when (unit) {
        KILO_PASCAL -> "%.0f kpa".format(kpa)
        BAR -> "%.2f bar".format(asBar())
        PSI -> "%.0f psi".format(asPsi())
    }

    fun hasPressure() = kpa > 0f

    operator fun compareTo(other: Pressure) = kpa.compareTo(other.kpa)

    private constructor(parcel: Parcel) : this(parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(kpa)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pressure> {

        val Float.kpa get() = Pressure(this)

        val Float.bar get() = Pressure(this.times(100))

        val Float.psi get() = Pressure(this * 6.895f)

        override fun createFromParcel(parcel: Parcel): Pressure {
            return Pressure(parcel)
        }

        override fun newArray(size: Int): Array<Pressure?> {
            return arrayOfNulls(size)
        }
    }
}
package com.masselis.tpmsadvanced.model

import android.os.Parcel
import android.os.Parcelable

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@JvmInline
value class Pressure(val kpa: Float) : Parcelable {

    fun asBar() = to(Unit.BAR)

    fun asPsi() = to(Unit.PSI)

    fun to(unit: Unit) = when (unit) {
        Unit.KILO_PASCAL -> kpa
        Unit.BAR -> kpa.div(100f)
        Unit.PSI -> kpa.div(6.895f)
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

        override fun createFromParcel(parcel: Parcel): Pressure {
            return Pressure(parcel)
        }

        override fun newArray(size: Int): Array<Pressure?> {
            return arrayOfNulls(size)
        }
    }

    enum class Unit {
        KILO_PASCAL,
        BAR,
        PSI;

        fun string() = when (this) {
            KILO_PASCAL -> "kpa"
            BAR -> "bar"
            PSI -> "psi"
        }
    }
}
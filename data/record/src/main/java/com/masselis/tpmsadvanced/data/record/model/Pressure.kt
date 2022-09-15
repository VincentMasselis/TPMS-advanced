package com.masselis.tpmsadvanced.data.record.model

import android.os.Parcel
import android.os.Parcelable
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.KILO_PASCAL
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.PSI

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@Suppress("MagicNumber")
@JvmInline
public value class Pressure(public val kpa: Float) : Parcelable {

    public fun asBar(): Float = convert(BAR)

    public fun asPsi(): Float = convert(PSI)

    public fun convert(unit: PressureUnit): Float = when (unit) {
        KILO_PASCAL -> kpa
        BAR -> kpa / 100f
        PSI -> kpa / 6.895f
    }

    public fun string(unit: PressureUnit): String = when (unit) {
        KILO_PASCAL -> "%.0f kpa".format(kpa)
        BAR -> "%.2f bar".format(asBar())
        PSI -> "%.0f psi".format(asPsi())
    }

    public fun hasPressure(): Boolean = kpa > 0f

    public operator fun compareTo(other: Pressure): Int = kpa.compareTo(other.kpa)

    private constructor(parcel: Parcel) : this(parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(kpa)
    }

    override fun describeContents(): Int = 0

    public companion object CREATOR : Parcelable.Creator<Pressure> {

        public val Float.kpa: Pressure get() = Pressure(this)

        public val Float.bar: Pressure get() = Pressure(this.times(100))

        public val Float.psi: Pressure get() = Pressure(this * 6.895f)

        override fun createFromParcel(parcel: Parcel): Pressure {
            return Pressure(parcel)
        }

        override fun newArray(size: Int): Array<Pressure?> {
            return arrayOfNulls(size)
        }
    }
}

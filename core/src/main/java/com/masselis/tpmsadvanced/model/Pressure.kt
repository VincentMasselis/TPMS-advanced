package com.masselis.tpmsadvanced.model

import android.os.Parcel
import android.os.Parcelable

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@JvmInline
value class Pressure(val kpa: Float) : Parcelable {

    val bar get() = kpa.div(100f)

    fun hasPressure() = kpa > 0f

    private constructor(parcel: Parcel) : this(parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(kpa)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pressure> {
        override fun createFromParcel(parcel: Parcel): Pressure {
            return Pressure(parcel)
        }

        override fun newArray(size: Int): Array<Pressure?> {
            return arrayOfNulls(size)
        }
    }
}
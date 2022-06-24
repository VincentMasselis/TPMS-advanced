package com.masselis.tpmsadvanced.model

import android.os.Parcel
import android.os.Parcelable

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@JvmInline
value class Temperature(val celsius: Float) : Parcelable {

    operator fun compareTo(other: Temperature) = celsius.compareTo(other.celsius)

    operator fun rangeTo(other: Temperature) = celsius.rangeTo(other.celsius)

    private constructor(parcel: Parcel) : this(parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(celsius)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Temperature> {
        override fun createFromParcel(parcel: Parcel): Temperature {
            return Temperature(parcel)
        }

        override fun newArray(size: Int): Array<Temperature?> {
            return arrayOfNulls(size)
        }
    }
}
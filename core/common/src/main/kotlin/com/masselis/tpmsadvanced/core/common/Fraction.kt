package com.masselis.tpmsadvanced.core.common

import android.os.Parcel
import android.os.Parcelable

@JvmInline
public value class Fraction(public val value: Float) : Parcelable {

    init {
        require(value in 0f..1f)
    }

    private constructor(parcel: Parcel) : this(parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(value)
    }

    override fun describeContents(): Int = 0

    internal companion object CREATOR : Parcelable.Creator<Fraction> {
        override fun createFromParcel(parcel: Parcel): Fraction {
            return Fraction(parcel)
        }

        override fun newArray(size: Int): Array<Fraction?> {
            return arrayOfNulls(size)
        }
    }
}

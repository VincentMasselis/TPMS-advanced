package com.masselis.tpmsadvanced.model

import android.os.Parcel
import android.os.Parcelable

@JvmInline
value class Fraction(val value: Float) : Parcelable {

    init {
        if (value !in 0f..1f)
            throw IllegalArgumentException()
    }

    private constructor(parcel: Parcel) : this(parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(value)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Fraction> {
        override fun createFromParcel(parcel: Parcel): Fraction {
            return Fraction(parcel)
        }

        override fun newArray(size: Int): Array<Fraction?> {
            return arrayOfNulls(size)
        }
    }
}
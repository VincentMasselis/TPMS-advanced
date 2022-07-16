package com.masselis.tpmsadvanced.model

import android.os.Parcel
import android.os.Parcelable

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@JvmInline
value class Temperature(val celsius: Float) : Parcelable, Comparable<Temperature> {

    fun asFahrenheit() = convert(Unit.FAHRENHEIT)

    fun convert(unit: Unit) = when (unit) {
        Unit.CELSIUS -> celsius
        Unit.FAHRENHEIT -> (celsius * (9f / 5f)) + 32f
    }

    fun string(unit: Unit) = when (unit) {
        Unit.CELSIUS -> "%.1f°C".format(celsius)
        Unit.FAHRENHEIT -> "%.0f°F".format(asFahrenheit())
    }

    override operator fun compareTo(other: Temperature) = celsius.compareTo(other.celsius)

    operator fun rangeTo(other: Temperature): ClosedFloatingPointRange<Temperature> =
        Range(this, other)

    private class Range(
        override val start: Temperature,
        override val endInclusive: Temperature
    ) : ClosedFloatingPointRange<Temperature> {
        override fun lessThanOrEquals(a: Temperature, b: Temperature): Boolean =
            a.celsius <= b.celsius
    }

    private constructor(parcel: Parcel) : this(parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(celsius)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Temperature> {

        val Float.celsius get() = Temperature(this)

        val Float.fahrenheit get() = Temperature((this - 32f) * (5f / 9f))

        override fun createFromParcel(parcel: Parcel): Temperature {
            return Temperature(parcel)
        }

        override fun newArray(size: Int): Array<Temperature?> {
            return arrayOfNulls(size)
        }
    }

    enum class Unit {
        CELSIUS,
        FAHRENHEIT;

        fun string() = when (this) {
            CELSIUS -> "celsius"
            FAHRENHEIT -> "fahrenheit"
        }
    }
}
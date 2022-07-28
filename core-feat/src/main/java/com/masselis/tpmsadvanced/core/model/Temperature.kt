package com.masselis.tpmsadvanced.core.model

import android.os.Parcel
import android.os.Parcelable
import com.masselis.tpmsadvanced.unit.model.TemperatureUnit

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@JvmInline
value class Temperature(val celsius: Float) : Parcelable, Comparable<Temperature> {

    fun asFahrenheit() = convert(TemperatureUnit.FAHRENHEIT)

    fun convert(unit: TemperatureUnit) = when (unit) {
        TemperatureUnit.CELSIUS -> celsius
        TemperatureUnit.FAHRENHEIT -> (celsius * (9f / 5f)) + 32f
    }

    fun string(unit: TemperatureUnit) = when (unit) {
        TemperatureUnit.CELSIUS -> "%.1f°C".format(celsius)
        TemperatureUnit.FAHRENHEIT -> "%.0f°F".format(asFahrenheit())
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
}
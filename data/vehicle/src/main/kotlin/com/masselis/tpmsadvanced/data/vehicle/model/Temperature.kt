package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcel
import android.os.Parcelable
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.FAHRENHEIT

/* Cannot use @Parcelize here: https://issuetracker.google.com/issues/177856519 */
@Suppress("MagicNumber")
@JvmInline
public value class Temperature(public val celsius: Float) : Parcelable, Comparable<Temperature> {

    public fun asFahrenheit(): Float = convert(FAHRENHEIT)

    public fun convert(unit: TemperatureUnit): Float = when (unit) {
        CELSIUS -> celsius
        FAHRENHEIT -> (celsius * (9f / 5f)) + 32f
    }

    public fun string(unit: TemperatureUnit): String = when (unit) {
        CELSIUS -> "%.1f°C".format(celsius)
        FAHRENHEIT -> "%.0f°F".format(asFahrenheit())
    }

    override operator fun compareTo(other: Temperature): Int = celsius.compareTo(other.celsius)

    public operator fun rangeTo(other: Temperature): ClosedFloatingPointRange<Temperature> =
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

    public companion object CREATOR : Parcelable.Creator<Temperature> {

        public val Float.celsius: Temperature get() = Temperature(this)

        public val Float.fahrenheit: Temperature get() = Temperature((this - 32f) * (5f / 9f))

        override fun createFromParcel(parcel: Parcel): Temperature {
            return Temperature(parcel)
        }

        override fun newArray(size: Int): Array<Temperature?> {
            return arrayOfNulls(size)
        }
    }
}

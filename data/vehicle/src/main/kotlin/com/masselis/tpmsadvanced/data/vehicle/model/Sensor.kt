package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public sealed interface Sensor : Parcelable {

    public val id: Int

    @JvmInline
    @Parcelize
    public value class Impl(override val id: Int) : Sensor

    @Parcelize
    public data class Located(override val id: Int, val location: SensorLocation) : Sensor
}

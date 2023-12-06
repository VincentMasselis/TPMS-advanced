package com.masselis.tpmsadvanced.qrcode.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
internal value class SensorMap private constructor(
    private val content: Map<SensorLocation, Sensor.Located>
) : Parcelable, Map<SensorLocation, Sensor.Located> by content {
    constructor(sensors: Collection<Sensor.Located>) : this(sensors
        .associateBy { it.location }
        .also { check(sensors.size == it.size) { "Filled argument \"sensors\" contains location duplicates" } }
    )
}

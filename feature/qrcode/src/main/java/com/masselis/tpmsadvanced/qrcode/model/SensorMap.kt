package com.masselis.tpmsadvanced.qrcode.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
internal value class SensorMap private constructor(
    private val content: Map<SensorLocation, Sensor>
) : Parcelable, Map<SensorLocation, Sensor> by content {
    constructor(sensors: Collection<Sensor>) : this(sensors
        .associateBy { it.location }
        .also { check(sensors.size == it.size) { "Filled argument \"sensors\" contains location duplicates" } }
    )
}

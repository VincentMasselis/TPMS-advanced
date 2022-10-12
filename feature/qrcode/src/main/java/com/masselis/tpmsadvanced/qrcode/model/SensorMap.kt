package com.masselis.tpmsadvanced.qrcode.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
internal value class SensorMap private constructor(
    private val content: Map<TyreLocation, Sensor>
) : Parcelable, Map<TyreLocation, Sensor> by content {
    constructor(vararg sensors: Sensor) : this(sensors
        .associateBy { it.location }
        .also { check(sensors.size == it.size) { "Filled argument \"sensors\" contains location duplicates" } }
    )
}

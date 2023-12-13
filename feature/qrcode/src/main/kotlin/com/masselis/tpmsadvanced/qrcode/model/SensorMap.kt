package com.masselis.tpmsadvanced.qrcode.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
internal value class SensorMap private constructor(
    private val content: Map<Location.Wheel, Sensor>
) : Parcelable, Map<Location.Wheel, Sensor> by content {
    constructor(sensors: Collection<Sensor>) : this(sensors
        .associateBy { it.location as Location.Wheel }
        .also { require(sensors.size == it.size) { "Filled argument \"sensors\" contains location duplicates" } }
    )
}

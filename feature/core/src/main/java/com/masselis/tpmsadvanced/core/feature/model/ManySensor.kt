package com.masselis.tpmsadvanced.core.feature.model

import androidx.compose.runtime.Stable
import com.masselis.tpmsadvanced.data.record.model.SensorLocation

@Stable
internal sealed interface ManySensor {
    val name: String

    @JvmInline
    value class Located(val location: SensorLocation) : ManySensor {
        override val name: String get() = location.name
    }

    @JvmInline
    value class Axle(val axle: SensorLocation.Axle) : ManySensor {
        override val name: String get() = axle.name
    }

    @JvmInline
    value class Side(val side: SensorLocation.Side) : ManySensor {
        override val name: String get() = side.name
    }
}
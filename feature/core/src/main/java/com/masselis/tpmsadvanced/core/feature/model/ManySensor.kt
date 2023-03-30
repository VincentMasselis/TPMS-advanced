package com.masselis.tpmsadvanced.core.feature.model

import androidx.compose.runtime.Stable
import com.masselis.tpmsadvanced.data.record.model.SensorLocation

@Stable
public sealed interface ManySensor {
    public val name: String

    @JvmInline
    public value class Located(public val location: SensorLocation) : ManySensor {
        override val name: String get() = location.name
    }

    @JvmInline
    public value class Axle(public val axle: SensorLocation.Axle) : ManySensor {
        override val name: String get() = axle.name
    }

    @JvmInline
    public value class Side(public val side: SensorLocation.Side) : ManySensor {
        override val name: String get() = side.name
    }
}

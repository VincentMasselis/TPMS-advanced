package com.masselis.tpmsadvanced.data.car.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.car.model.Vehicle.ManySensor.Axle
import com.masselis.tpmsadvanced.data.car.model.Vehicle.ManySensor.Located
import com.masselis.tpmsadvanced.data.car.model.Vehicle.ManySensor.Side
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.record.model.Temperature
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
public data class Vehicle(
    public val uuid: UUID,
    public val kind: Kind,
    public val name: String,
    public val lowPressure: Pressure,
    public val highPressure: Pressure,
    public val lowTemp: Temperature,
    public val normalTemp: Temperature,
    public val highTemp: Temperature,
    public val isBackgroundMonitor: Boolean,
) : Parcelable {

    public enum class Kind(public val locations: Set<ManySensor>) {
        /**
         * ```
         * N-N
         *  |
         * N-N
         * ```
         */
        CAR(
            setOf(
                Located(FRONT_LEFT),
                Located(FRONT_RIGHT),
                Located(REAR_LEFT),
                Located(REAR_RIGHT)
            )
        ),

        /**
         * ```
         * N-N
         * ```
         */
        SINGLE_AXLE_TRAILER(setOf(Side(LEFT), Side(RIGHT))),

        /**
         * ```
         *  N
         *  |
         *  N
         * ```
         */
        MOTORCYCLE(setOf(Axle(FRONT), Axle(REAR))),

        /**
         * ```
         * N-N
         *  N
         * ```
         */
        TADPOLE_THREE_WHEELER(setOf(Located(FRONT_LEFT), Located(FRONT_RIGHT), Axle(REAR))),

        /**
         * ```
         *  N
         * N-N
         * ```
         */
        DELTA_THREE_WHEELER(setOf(Axle(FRONT), Located(REAR_LEFT), Located(REAR_RIGHT)));
    }

    public sealed interface ManySensor {
        @JvmInline
        public value class Located(public val location: SensorLocation) : ManySensor

        @JvmInline
        public value class Axle(public val axle: SensorLocation.Axle) : ManySensor

        @JvmInline
        public value class Side(public val side: SensorLocation.Side) : ManySensor
    }
}

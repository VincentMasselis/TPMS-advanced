package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Axle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Side
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
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

    /**
     * Defines the layout of a vehicle.
     *
     * For each [Kind], the property [locations] represent the current layout with a [Set] of
     * [Location].
     */
    public enum class Kind(public val locations: Set<Location>) {
        /**
         * ```
         * N-N
         *  |
         * N-N
         * ```
         */
        CAR(
            setOf(
                Wheel(FRONT_LEFT),
                Wheel(FRONT_RIGHT),
                Wheel(REAR_LEFT),
                Wheel(REAR_RIGHT)
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
        TADPOLE_THREE_WHEELER(setOf(Wheel(FRONT_LEFT), Wheel(FRONT_RIGHT), Axle(REAR))),

        /**
         * ```
         *  N
         * N-N
         * ```
         */
        DELTA_THREE_WHEELER(setOf(Axle(FRONT), Wheel(REAR_LEFT), Wheel(REAR_RIGHT)));

        /**
         * Unlike [SensorLocation] which represents a location from the sensor standpoint, a
         * [Location] represents a location from a [Vehicle] standpoint.
         */
        public sealed interface Location : Parcelable {
            @JvmInline
            @Parcelize
            public value class Wheel(public val location: SensorLocation) : Location {
                public fun toAxle(): Axle = Axle(location.axle)
                public fun toSide(): Side = Side(location.side)

                override fun toString(): String {
                    return when (location) {
                        FRONT_LEFT -> "Front Left"
                        FRONT_RIGHT -> "Front Right"
                        REAR_LEFT -> "Rear Left"
                        REAR_RIGHT -> "Rear Right"
                    }
                }
            }

            @JvmInline
            @Parcelize
            public value class Axle(public val axle: SensorLocation.Axle) : Location {
                override fun toString(): String {
                    return when (axle) {
                        FRONT -> "Front"
                        REAR -> "Rear"
                    }
                }
            }

            @JvmInline
            @Parcelize
            public value class Side(public val side: SensorLocation.Side) : Location {
                override fun toString(): String {
                    return when (side) {
                        LEFT -> "Left"
                        RIGHT -> "Right"
                    }
                }
            }
        }
    }
}

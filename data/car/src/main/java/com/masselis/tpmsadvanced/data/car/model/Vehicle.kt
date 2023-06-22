package com.masselis.tpmsadvanced.data.car.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.Location.Axle
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.Location.Side
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.Location.Wheel
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
         * Computes the [Set] of [Location] according to the filled [sensorLocations].
         *
         * While [locations] returns the complete list of [Location] required for the current [Kind],
         * [computeLocations] returns only a list of [Location] which were found in the list
         * [sensorLocations].
         */
        @Suppress("CyclomaticComplexMethod")
        public fun computeLocations(
            sensorLocations: Set<SensorLocation>
        ): Set<Location> = when (this) {
            CAR -> sensorLocations.map { Wheel(it) }.toSet()

            SINGLE_AXLE_TRAILER -> setOfNotNull(
                if (sensorLocations.any { it.side == LEFT }) Side(LEFT) else null,
                if (sensorLocations.any { it.side == RIGHT }) Side(RIGHT) else null,
            )

            MOTORCYCLE -> setOfNotNull(
                if (sensorLocations.any { it.axle == FRONT }) Axle(FRONT) else null,
                if (sensorLocations.any { it.axle == REAR }) Axle(REAR) else null,
            )

            TADPOLE_THREE_WHEELER -> setOfNotNull(
                if (sensorLocations.any { it.axle == FRONT }) Axle(FRONT) else null,
                sensorLocations.firstOrNull { it == REAR_LEFT }?.let { Wheel(it) },
                sensorLocations.firstOrNull { it == REAR_RIGHT }?.let { Wheel(it) },
            )

            DELTA_THREE_WHEELER -> setOfNotNull(
                sensorLocations.firstOrNull { it == FRONT_LEFT }?.let { Wheel(it) },
                sensorLocations.firstOrNull { it == FRONT_RIGHT }?.let { Wheel(it) },
                if (sensorLocations.any { it.axle == REAR }) Axle(REAR) else null,
            )
        }

        /**
         * Unlike [SensorLocation] which always represents a sensor attached to a wheel, [Location]
         * represents a [Wheel] an [Axle] or a [Side].
         */
        public sealed interface Location {
            @JvmInline
            public value class Wheel(public val location: SensorLocation) : Location

            @JvmInline
            public value class Axle(public val axle: SensorLocation.Axle) : Location

            @JvmInline
            public value class Side(public val side: SensorLocation.Side) : Location
        }
    }

}

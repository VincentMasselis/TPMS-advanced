package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_RIGHT
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
public data class Vehicle(
    public val uuid: UUID,
    public val kind: Kind,
    public val name: String,
    public val lowPressure: Pressure,
    public val highPressure: Pressure,
    public val separateFrontRearPressure: Boolean,
    public val rearLowPressure: Pressure,
    public val rearHighPressure: Pressure,
    public val lowTemp: Temperature,
    public val normalTemp: Temperature,
    public val highTemp: Temperature,
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
        CAR(setOf(FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT)),

        /**
         * ```
         * N-N
         * ```
         */
        SINGLE_AXLE_TRAILER(setOf(LEFT, RIGHT)),

        /**
         * ```
         *  N
         *  |
         *  N
         * ```
         */
        MOTORCYCLE(setOf(FRONT, REAR)),

        /**
         * ```
         * N-N
         *  N
         * ```
         */
        TADPOLE_THREE_WHEELER(setOf(FRONT_LEFT, FRONT_RIGHT, REAR)),

        /**
         * ```
         *  N
         * N-N
         * ```
         */
        DELTA_THREE_WHEELER(setOf(FRONT, REAR_LEFT, REAR_RIGHT));

    }
}

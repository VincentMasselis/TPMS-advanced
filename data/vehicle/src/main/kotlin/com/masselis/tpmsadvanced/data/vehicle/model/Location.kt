package com.masselis.tpmsadvanced.data.vehicle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

public sealed interface Location : Parcelable {
    @Parcelize
    public enum class Wheel : Location {
        FRONT_LEFT,
        FRONT_RIGHT,
        REAR_LEFT,
        REAR_RIGHT;

        public fun toAxle(): Axle = when (this) {
            FRONT_LEFT, FRONT_RIGHT -> Axle.FRONT
            REAR_LEFT, REAR_RIGHT -> Axle.REAR
        }

        public fun toSide(): Side = when (this) {
            FRONT_LEFT, REAR_LEFT -> Side.LEFT
            FRONT_RIGHT, REAR_RIGHT -> Side.RIGHT
        }
    }

    @Parcelize
    public enum class Axle : Location {
        FRONT,
        REAR;
    }

    @Parcelize
    public enum class Side : Location {
        LEFT,
        RIGHT;
    }
}

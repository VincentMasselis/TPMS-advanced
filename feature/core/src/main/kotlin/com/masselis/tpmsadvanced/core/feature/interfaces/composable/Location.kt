package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import java.lang.StringBuilder

public fun StringBuilder.appendLoc(location: Vehicle.Kind.Location): StringBuilder =
    when (location) {
        is Vehicle.Kind.Location.Wheel -> {
            append(
                when (location.location) {
                    SensorLocation.FRONT_LEFT -> "front left"
                    SensorLocation.FRONT_RIGHT -> "front right"
                    SensorLocation.REAR_LEFT -> "rear left"
                    SensorLocation.REAR_RIGHT -> "rear right"
                }
            )
            append(" wheel")
        }

        is Vehicle.Kind.Location.Axle -> {
            append(
                when (location.axle) {
                    SensorLocation.Axle.FRONT -> "front"
                    SensorLocation.Axle.REAR -> "rear"
                }
            )
            append(" axle")
        }

        is Vehicle.Kind.Location.Side -> {
            append(
                when (location.side) {
                    SensorLocation.Side.LEFT -> "left"
                    SensorLocation.Side.RIGHT -> "right"
                }
            )
            append(" side")
        }
    }
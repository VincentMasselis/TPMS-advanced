package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import java.util.Locale

public fun StringBuilder.appendLoc(
    location: Vehicle.Kind.Location,
    withType: Boolean = true,
    capitalized: Boolean = false,
): StringBuilder = when (location) {
    is Vehicle.Kind.Location.Wheel -> {
        append(
            when (location.location) {
                SensorLocation.FRONT_LEFT -> "front left".capitalizeIf(capitalized)
                SensorLocation.FRONT_RIGHT -> "front right".capitalizeIf(capitalized)
                SensorLocation.REAR_LEFT -> "rear left".capitalizeIf(capitalized)
                SensorLocation.REAR_RIGHT -> "rear right".capitalizeIf(capitalized)
            }
        )
        appendIf(withType, " wheel")
    }

    is Vehicle.Kind.Location.Axle -> {
        append(
            when (location.axle) {
                SensorLocation.Axle.FRONT -> "front".capitalizeIf(capitalized)
                SensorLocation.Axle.REAR -> "rear".capitalizeIf(capitalized)
            }
        )
        appendIf(withType, " axle")
    }

    is Vehicle.Kind.Location.Side -> {
        append(
            when (location.side) {
                SensorLocation.Side.LEFT -> "left".capitalizeIf(capitalized)
                SensorLocation.Side.RIGHT -> "right".capitalizeIf(capitalized)
            }
        )
        appendIf(withType, " side")
    }
}

private fun StringBuilder.appendIf(condition: Boolean, string: String) =
    if (condition) append(string) else this

private fun String.capitalizeIf(condition: Boolean) =
    if (condition) replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
    else
        this

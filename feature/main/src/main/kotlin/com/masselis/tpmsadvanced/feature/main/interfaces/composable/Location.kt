package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_RIGHT
import java.util.Locale

public fun StringBuilder.appendLoc(
    location: Location,
    withType: Boolean = true,
    capitalized: Boolean = false,
): StringBuilder = when (location) {
    is Wheel -> {
        append(
            when (location) {
                FRONT_LEFT -> "front left".capitalizeIf(capitalized)
                FRONT_RIGHT -> "front right".capitalizeIf(capitalized)
                REAR_LEFT -> "rear left".capitalizeIf(capitalized)
                REAR_RIGHT -> "rear right".capitalizeIf(capitalized)
            }
        )
        appendIf(withType, " wheel")
    }

    is Location.Axle -> {
        append(
            when (location) {
                FRONT -> "front".capitalizeIf(capitalized)
                REAR -> "rear".capitalizeIf(capitalized)
            }
        )
        appendIf(withType, " axle")
    }

    is Location.Side -> {
        append(
            when (location) {
                LEFT -> "left".capitalizeIf(capitalized)
                RIGHT -> "right".capitalizeIf(capitalized)
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

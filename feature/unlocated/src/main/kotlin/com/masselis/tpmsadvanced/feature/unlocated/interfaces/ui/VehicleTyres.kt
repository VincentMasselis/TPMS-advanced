package com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.ui.VehicleTyresTags.tyreLocation
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Suppress("NAME_SHADOWING")
@Composable
internal fun Vehicle(
    kind: Vehicle.Kind,
    states: ImmutableMap<Location, WheelState>,
    modifier: Modifier = Modifier,
    onWheelTap: (Location?) -> Unit = onWheelTapPlaceholder,
) {
    val modifier = modifier.aspectRatio(.5f)
    when (kind) {
        Vehicle.Kind.CAR -> Car(
            frontLeft = states.getValue(FRONT_LEFT),
            frontRight = states.getValue(FRONT_RIGHT),
            rearLeft = states.getValue(REAR_LEFT),
            rearRight = states.getValue(REAR_RIGHT),
            onWheelTap = onWheelTap,
            modifier = modifier,
        )

        Vehicle.Kind.SINGLE_AXLE_TRAILER -> SingleAxleTrailer(
            left = states.getValue(LEFT),
            right = states.getValue(RIGHT),
            onWheelTap = onWheelTap,
            modifier = modifier
        )

        Vehicle.Kind.MOTORCYCLE -> Motorcycle(
            front = states.getValue(FRONT),
            rear = states.getValue(REAR),
            onWheelTap = onWheelTap,
            modifier = modifier
        )

        Vehicle.Kind.TADPOLE_THREE_WHEELER -> TadpoleThreeWheeler(
            frontLeft = states.getValue(FRONT_LEFT),
            frontRight = states.getValue(FRONT_RIGHT),
            rear = states.getValue(REAR),
            onWheelTap = onWheelTap,
            modifier = modifier
        )

        Vehicle.Kind.DELTA_THREE_WHEELER -> DeltaThreeWheeler(
            front = states.getValue(FRONT),
            rearLeft = states.getValue(REAR_LEFT),
            rearRight = states.getValue(REAR_RIGHT),
            onWheelTap = onWheelTap,
            modifier = modifier
        )
    }
}

public sealed interface WheelState {
    public data object Empty : WheelState
    public data object Fade : WheelState
    public data object Highlighted : WheelState
}

@Composable
private fun Car(
    frontLeft: WheelState,
    frontRight: WheelState,
    rearLeft: WheelState,
    rearRight: WheelState,
    onWheelTap: (Location?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Tyre(
            state = frontLeft,
            onTap = onWheelTap.takeIfImplemented(FRONT_LEFT),
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag(tyreLocation(FRONT_LEFT))
        )
        Tyre(
            state = frontRight,
            onTap = onWheelTap.takeIfImplemented(FRONT_RIGHT),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .testTag(tyreLocation(FRONT_RIGHT))
        )
        Tyre(
            state = rearLeft,
            onTap = onWheelTap.takeIfImplemented(REAR_LEFT),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .testTag(tyreLocation(REAR_LEFT))
        )
        Tyre(
            state = rearRight,
            onTap = onWheelTap.takeIfImplemented(REAR_RIGHT),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .testTag(tyreLocation(REAR_RIGHT))
        )
    }
}

@Composable
private fun SingleAxleTrailer(
    left: WheelState,
    right: WheelState,
    onWheelTap: (Location?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Tyre(
            state = left,
            onTap = onWheelTap.takeIfImplemented(LEFT),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .testTag(tyreLocation(LEFT)),
        )
        Tyre(
            state = right,
            onTap = onWheelTap.takeIfImplemented(RIGHT),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .testTag(tyreLocation(RIGHT)),
        )
    }
}

@Composable
private fun Motorcycle(
    front: WheelState,
    rear: WheelState,
    onWheelTap: (Location?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Tyre(
            state = front,
            onTap = onWheelTap.takeIfImplemented(FRONT),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .testTag(tyreLocation(FRONT)),
        )
        Tyre(
            state = rear,
            onTap = onWheelTap.takeIfImplemented(REAR),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .testTag(tyreLocation(REAR)),
        )
    }
}

@Composable
private fun TadpoleThreeWheeler(
    frontLeft: WheelState,
    frontRight: WheelState,
    rear: WheelState,
    onWheelTap: (Location?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Tyre(
            state = frontLeft,
            onTap = onWheelTap.takeIfImplemented(FRONT_LEFT),
            modifier = Modifier
                .align(Alignment.TopStart)
                .testTag(tyreLocation(FRONT_LEFT))
        )
        Tyre(
            state = frontRight,
            onTap = onWheelTap.takeIfImplemented(FRONT_RIGHT),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .testTag(tyreLocation(FRONT_RIGHT))
        )
        Tyre(
            state = rear,
            onTap = onWheelTap.takeIfImplemented(REAR),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .testTag(tyreLocation(REAR))
        )
    }
}

@Composable
private fun DeltaThreeWheeler(
    front: WheelState,
    rearLeft: WheelState,
    rearRight: WheelState,
    onWheelTap: (Location?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Tyre(
            state = front,
            onTap = onWheelTap.takeIfImplemented(FRONT),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .testTag(tyreLocation(FRONT))
        )
        Tyre(
            state = rearLeft,
            onTap = onWheelTap.takeIfImplemented(REAR_LEFT),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .testTag(tyreLocation(REAR_LEFT))
        )
        Tyre(
            state = rearRight,
            onTap = onWheelTap.takeIfImplemented(REAR_RIGHT),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .testTag(tyreLocation(REAR_RIGHT))
        )
    }
}

@Composable
private fun Tyre(
    state: WheelState,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = onTapPlaceholder,
) {
    BoxWithConstraints(modifier) {
        Surface(
            shape = RoundedCornerShape(percent = if (maxHeight >= 150.dp) 20 else 40),
            color = when (state) {
                WheelState.Empty -> Color.Transparent
                WheelState.Fade, WheelState.Highlighted -> MaterialTheme.colorScheme.primary
            },
            border = when (state) {
                WheelState.Empty -> BorderStroke(
                    if (maxHeight >= 150.dp) 2.dp else 1.dp,
                    MaterialTheme.colorScheme.onBackground
                )

                WheelState.Fade, WheelState.Highlighted -> null
            },
            enabled = onTap !== onTapPlaceholder,
            onClick = onTap,
            modifier = Modifier
                .run {
                    if (state is WheelState.Fade) alpha(0.5f)
                    else this
                }
                .fillMaxHeight(.33f)
                .aspectRatio(15f / 40f)
        ) {}
    }
}

private val onTapPlaceholder: () -> Unit = {}
private val onWheelTapPlaceholder: (Location?) -> Unit = {}
private fun ((Location?) -> Unit).takeIfImplemented(location: Location): () -> Unit =
    if (this === onWheelTapPlaceholder) onTapPlaceholder
    else {
        { invoke(location) }
    }

@Preview
@Composable
internal fun CarPreview() {
    Vehicle(
        kind = Vehicle.Kind.CAR,
        states = persistentMapOf(
            FRONT_LEFT to WheelState.Highlighted,
            FRONT_RIGHT to WheelState.Fade,
            REAR_LEFT to WheelState.Empty,
            REAR_RIGHT to WheelState.Empty
        )
    )
}

@Preview
@Composable
internal fun SingleAxleTrailerPreview() {
    Vehicle(
        kind = Vehicle.Kind.SINGLE_AXLE_TRAILER,
        states = persistentMapOf(
            LEFT to WheelState.Highlighted,
            RIGHT to WheelState.Empty,
        )
    )
}

@Preview
@Composable
internal fun MotorcyclePreview() {
    Vehicle(
        kind = Vehicle.Kind.MOTORCYCLE,
        states = persistentMapOf(
            FRONT to WheelState.Highlighted,
            REAR to WheelState.Empty,
        )
    )
}

@Preview
@Composable
internal fun TadpoleThreeWheelerPreview() {
    Vehicle(
        kind = Vehicle.Kind.TADPOLE_THREE_WHEELER,
        states = persistentMapOf(
            FRONT_LEFT to WheelState.Highlighted,
            FRONT_RIGHT to WheelState.Fade,
            REAR to WheelState.Empty,
        )
    )
}

@Preview
@Composable
internal fun DeltaThreeWheelerPreview() {
    Vehicle(
        kind = Vehicle.Kind.DELTA_THREE_WHEELER,
        states = persistentMapOf(
            FRONT to WheelState.Highlighted,
            REAR_LEFT to WheelState.Fade,
            REAR_RIGHT to WheelState.Empty,
        )
    )
}

@Preview(showBackground = true)
@Composable
internal fun CarNanoPreview() {
    Vehicle(
        kind = Vehicle.Kind.CAR,
        states = persistentMapOf(
            FRONT_LEFT to WheelState.Highlighted,
            FRONT_RIGHT to WheelState.Fade,
            REAR_LEFT to WheelState.Empty,
            REAR_RIGHT to WheelState.Empty
        ),
        modifier = Modifier.height(48.dp),
    )
}

@Preview(showBackground = true)
@Composable
internal fun CarMinusPreview() {
    Vehicle(
        kind = Vehicle.Kind.CAR,
        states = persistentMapOf(
            FRONT_LEFT to WheelState.Highlighted,
            FRONT_RIGHT to WheelState.Fade,
            REAR_LEFT to WheelState.Empty,
            REAR_RIGHT to WheelState.Empty
        ),
        modifier = Modifier.height(100.dp),
    )
}

@Preview(showBackground = true)
@Composable
internal fun CarAveragePreview() {
    Vehicle(
        kind = Vehicle.Kind.CAR,
        states = persistentMapOf(
            FRONT_LEFT to WheelState.Highlighted,
            FRONT_RIGHT to WheelState.Fade,
            REAR_LEFT to WheelState.Empty,
            REAR_RIGHT to WheelState.Empty
        ),
        modifier = Modifier.height(200.dp),
    )
}

@Preview(showBackground = true)
@Composable
internal fun CarBigPreview() {
    Vehicle(
        kind = Vehicle.Kind.CAR,
        states = persistentMapOf(
            FRONT_LEFT to WheelState.Highlighted,
            FRONT_RIGHT to WheelState.Fade,
            REAR_LEFT to WheelState.Empty,
            REAR_RIGHT to WheelState.Empty
        ),
        modifier = Modifier.height(400.dp),
    )
}

internal object VehicleTyresTags {
    fun tyreLocation(location: Location) = "tyreLocation_$location"
}

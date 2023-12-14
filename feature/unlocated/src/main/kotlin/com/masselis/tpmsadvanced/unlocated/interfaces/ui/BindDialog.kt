package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.DELTA_THREE_WHEELER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.SINGLE_AXLE_TRAILER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.TADPOLE_THREE_WHEELER
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.BindSensorViewModel
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.BindSensorViewModel.State
import com.masselis.tpmsadvanced.unlocated.ioc.FeatureUnlocatedBinding.Companion.BindSensorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

@Composable
internal fun BindDialog(
    vehicleUuid: UUID,
    tyre: Tyre,
    onBound: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: BindSensorViewModel = viewModel(key = "BindSensorViewModel_${vehicleUuid}_${tyre}") {
        BindSensorViewModel(vehicleUuid, tyre, createSavedStateHandle())
    }
) {
    val state = viewModel.stateFlow.collectAsState().value
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Ready to bind") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state is State.BoundToAnOtherVehicle) {
                    Text(
                        text = "⚠️ This sensor is already bound to the vehicle " +
                                "\"${state.boundVehicle.name}\" and attached to the " +
                                state.boundVehicleLocation.let {
                                    when (it) {
                                        is Location.Axle -> when (it.axle) {
                                            FRONT -> "front"
                                            REAR -> "rear"
                                        }

                                        is Location.Side -> when (it.side) {
                                            LEFT -> "left"
                                            RIGHT -> "right"
                                        }

                                        is Location.Wheel -> when (it.location) {
                                            FRONT_LEFT -> "front left"
                                            FRONT_RIGHT -> "front right"
                                            REAR_LEFT -> "rear left"
                                            REAR_RIGHT -> "rear right"
                                        }
                                    }
                                } +
                                " wheel"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = "Tap the wheel:",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (state.currentVehicle.kind) {
                    Vehicle.Kind.CAR -> Car(
                        boundLocations = state.alreadyBoundLocations,
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    SINGLE_AXLE_TRAILER -> SingleAxleTrailer(
                        boundLocations = state.alreadyBoundLocations,
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    Vehicle.Kind.MOTORCYCLE -> Motorcycle(
                        boundLocations = state.alreadyBoundLocations,
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    Vehicle.Kind.TADPOLE_THREE_WHEELER -> TadpoleThreeWheeler(
                        boundLocations = state.alreadyBoundLocations,
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )

                    Vehicle.Kind.DELTA_THREE_WHEELER -> DeltaThreeWheeler(
                        boundLocations = state.alreadyBoundLocations,
                        selectedLocation = selectedLocation,
                        onSelectedLocation = { selectedLocation = it }
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedLocation != null,
                onClick = {
                    viewModel.bind(selectedLocation!!)
                    onBound()
                }
            ) {
                Text(text = "Bind")
            }
        }
    )
}

@Composable
private fun Car(
    boundLocations: Set<Location>,
    selectedLocation: Location?,
    onSelectedLocation: (Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Location.Wheel(FRONT_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        with(Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        with(Location.Wheel(REAR_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
        with(Location.Wheel(REAR_RIGHT)) {
            Tyre(
                isEnabled = boundLocations.contains(this).not(),
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun SingleAxleTrailer(
    boundLocations: Set<Location>,
    selectedLocation: Location?,
    onSelectedLocation: (Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Location.Side(LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        with(Location.Side(RIGHT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun Motorcycle(
    boundLocations: Set<Location>,
    selectedLocation: Location?,
    onSelectedLocation: (Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Location.Axle(FRONT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        with(Location.Axle(REAR)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun TadpoleThreeWheeler(
    boundLocations: Set<Location>,
    selectedLocation: Location?,
    onSelectedLocation: (Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Location.Wheel(FRONT_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        with(Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        with(Location.Axle(REAR)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun DeltaThreeWheeler(
    boundLocations: Set<Location>,
    selectedLocation: Location?,
    onSelectedLocation: (Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Location.Axle(FRONT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        with(Location.Wheel(REAR_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
        with(Location.Wheel(REAR_RIGHT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun Tyre(
    isSelected: Boolean,
    isEnabled: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .run {
                if (isEnabled.not()) alpha(0.5f)
                else this
            }
            .clip(RoundedCornerShape(percent = 20))
            .height(35.dp)
            .aspectRatio(15f / 40f)
            .run {
                if (isSelected)
                    background(MaterialTheme.colorScheme.primary)
                else
                    background(Color.Transparent)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.onBackground,
                            RoundedCornerShape(percent = 20)
                        )
            }
            .run {
                if (isEnabled) clickable(onClick = onTap)
                else this
            }
    )
}


@Preview(showBackground = true)
@Composable
private fun BindDialogCarPreview() {
    BindDialog(
        UUID.randomUUID(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                mockVehicle(),
                setOf(Location.Wheel(FRONT_LEFT))
            )
        ),
        onBound = {},
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogTrailerPreview() {
    BindDialog(
        UUID.randomUUID(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                mockVehicle(kind = SINGLE_AXLE_TRAILER),
                setOf(Location.Side(LEFT))
            )
        ),
        onBound = {},
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogMotorcyclePreview() {
    BindDialog(
        UUID.randomUUID(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                mockVehicle(kind = MOTORCYCLE),
                setOf(Location.Axle(FRONT))
            )
        ),
        onBound = {},
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogTadpolePreview() {
    BindDialog(
        UUID.randomUUID(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                mockVehicle(kind = TADPOLE_THREE_WHEELER),
                setOf(Location.Wheel(FRONT_LEFT))
            )
        ),
        onBound = {},
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogDeltaPreview() {
    BindDialog(
        UUID.randomUUID(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                mockVehicle(kind = DELTA_THREE_WHEELER),
                setOf(Location.Axle(FRONT))
            )
        ),
        onBound = {},
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogAlreadyBoundPreview() {
    BindDialog(
        UUID.randomUUID(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.BoundToAnOtherVehicle(
                mockVehicle(),
                emptySet(),
                mockVehicle(),
                Location.Wheel(FRONT_LEFT)
            )
        ),
        onBound = {},
        onDismissRequest = {},
    )
}

private class MockViewModel(state: State) : BindSensorViewModel {
    override val stateFlow: StateFlow<State> = MutableStateFlow(state)

    override fun bind(location: Location) = error("")

}
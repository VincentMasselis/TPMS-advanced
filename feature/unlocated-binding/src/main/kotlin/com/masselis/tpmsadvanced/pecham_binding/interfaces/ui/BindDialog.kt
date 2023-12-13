package com.masselis.tpmsadvanced.pecham_binding.interfaces.ui

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
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.SINGLE_AXLE_TRAILER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.TADPOLE_THREE_WHEELER
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.BindSensorViewModel
import com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel.BindSensorViewModel.State
import com.masselis.tpmsadvanced.pecham_binding.ioc.FeatureUnlocatedBinding.Companion.BindSensorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

@Composable
internal fun BindDialog(
    vehicle: Vehicle,
    tyre: Tyre,
    onDismissRequest: () -> Unit,
    viewModel: BindSensorViewModel = viewModel(key = "BindSensorViewModel_${vehicle.uuid}_${tyre}") {
        BindSensorViewModel(vehicle, tyre, createSavedStateHandle())
    }
) {
    val state = viewModel.stateFlow.collectAsState().value
    var selectedLocation by remember { mutableStateOf<Vehicle.Kind.Location?>(null) }
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
                                        is Vehicle.Kind.Location.Axle -> when (it.axle) {
                                            FRONT -> "front"
                                            REAR -> "rear"
                                        }

                                        is Vehicle.Kind.Location.Side -> when (it.side) {
                                            LEFT -> "left"
                                            RIGHT -> "right"
                                        }

                                        is Vehicle.Kind.Location.Wheel -> when (it.location) {
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
                    text =
                    if (state is State.BoundToAnOtherVehicle) "Tap a wheel to replace the binding:"
                    else "Tap the wheel to bind:",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (vehicle.kind) {
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
                    onDismissRequest()
                }
            ) {
                Text(text = "Bind")
            }
        }
    )
}

@Composable
private fun Car(
    boundLocations: Set<Vehicle.Kind.Location>,
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Wheel(FRONT_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_RIGHT)) {
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
    boundLocations: Set<Vehicle.Kind.Location>,
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Side(LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        with(Vehicle.Kind.Location.Side(RIGHT)) {
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
    boundLocations: Set<Vehicle.Kind.Location>,
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Axle(FRONT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        with(Vehicle.Kind.Location.Axle(REAR)) {
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
    boundLocations: Set<Vehicle.Kind.Location>,
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Wheel(FRONT_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(FRONT_RIGHT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        with(Vehicle.Kind.Location.Axle(REAR)) {
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
    boundLocations: Set<Vehicle.Kind.Location>,
    selectedLocation: Vehicle.Kind.Location?,
    onSelectedLocation: (Vehicle.Kind.Location?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.size(50.dp, 100.dp)
    ) {
        with(Vehicle.Kind.Location.Axle(FRONT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_LEFT)) {
            Tyre(
                isEnabled = boundLocations.none { it == this },
                isSelected = selectedLocation == this || boundLocations.any { it == this },
                onTap = { onSelectedLocation(this) },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
        with(Vehicle.Kind.Location.Wheel(REAR_RIGHT)) {
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
        mockVehicle(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                setOf(
                    Vehicle.Kind.Location.Wheel(FRONT_LEFT)
                )
            )
        ),
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogTrailerPreview() {
    BindDialog(
        mockVehicle(kind = SINGLE_AXLE_TRAILER),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                setOf(
                    Vehicle.Kind.Location.Side(LEFT)
                )
            )
        ),
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogMotorcyclePreview() {
    BindDialog(
        mockVehicle(kind = MOTORCYCLE),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                setOf(
                    Vehicle.Kind.Location.Axle(FRONT)
                )
            )
        ),
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogTadpolePreview() {
    BindDialog(
        mockVehicle(kind = TADPOLE_THREE_WHEELER),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                setOf(
                    Vehicle.Kind.Location.Wheel(FRONT_LEFT)
                )
            )
        ),
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogDeltaPreview() {
    BindDialog(
        mockVehicle(kind = DELTA_THREE_WHEELER),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.ReadyToBind(
                setOf(
                    Vehicle.Kind.Location.Axle(FRONT)
                )
            )
        ),
        onDismissRequest = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogAlreadyBoundPreview() {
    BindDialog(
        mockVehicle(),
        tyre = mockTyre(1),
        viewModel = MockViewModel(
            State.BoundToAnOtherVehicle(
                emptySet(),
                mockVehicle(),
                Vehicle.Kind.Location.Wheel(FRONT_LEFT)
            )
        ),
        onDismissRequest = {},
    )
}

private class MockViewModel(state: State) : BindSensorViewModel {
    override val stateFlow: StateFlow<State> = MutableStateFlow(state)

    override fun bind(location: Vehicle.Kind.Location) = error("")

}
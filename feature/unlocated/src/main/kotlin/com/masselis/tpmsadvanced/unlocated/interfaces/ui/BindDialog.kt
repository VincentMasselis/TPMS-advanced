package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.appendLoc
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.DELTA_THREE_WHEELER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.SINGLE_AXLE_TRAILER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.TADPOLE_THREE_WHEELER
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.BindDialogTags.bindButton
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.BindDialogTags.root
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.BindDialogTags.cancelButton
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.BindDialogViewModel
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.BindDialogViewModel.State
import com.masselis.tpmsadvanced.unlocated.ioc.FeatureUnlocatedBinding.Companion.BindDialogViewModel
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

@Suppress("NAME_SHADOWING", "LongMethod")
@Composable
internal fun BindDialog(
    vehicleUuid: UUID,
    tyre: Tyre,
    onBound: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: BindDialogViewModel = viewModel(key = "BindSensorViewModel_${vehicleUuid}_${tyre}") {
        BindDialogViewModel(vehicleUuid, tyre, createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    val wheelStates by remember {
        derivedStateOf {
            state.currentVehicle
                .kind
                .locations
                .associateWith {
                    when {
                        it == selectedLocation -> WheelState.Highlighted
                        state.alreadyBoundLocations.contains(it) -> WheelState.Fade
                        else -> WheelState.Empty
                    }
                }
                .toImmutableMap()
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Ready to bind") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state is State.BoundToAnOtherVehicle) {
                    val state = state as State.BoundToAnOtherVehicle
                    Text(
                        text = StringBuilder("⚠️ This sensor is already bound to the vehicle ")
                            .append("\"${state.boundVehicle.name}\" and attached to the ")
                            .appendLoc(state.boundVehicleLocation)
                            .append(".")
                            .toString()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = "Tap the wheel:",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Vehicle(
                    kind = state.currentVehicle.kind,
                    states = wheelStates,
                    onWheelTap = onWheelTap@{
                        if (state.alreadyBoundLocations.contains(it))
                            return@onWheelTap
                        selectedLocation = it
                    },
                    modifier = Modifier.height(150.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag(cancelButton)
            ) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedLocation != null,
                onClick = {
                    viewModel.bind(selectedLocation!!)
                    onBound()
                },
                modifier = Modifier.testTag(bindButton)
            ) {
                Text(text = "Bind")
            }
        },
        modifier = Modifier.testTag(root)
    )
}

@Preview(showBackground = true)
@Composable
private fun BindDialogCarPreview() {
    BindDialog(
        UUID.randomUUID(),
        tyre = mockTyre(1),
        viewModel = MockBindDialogViewModel(
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
        viewModel = MockBindDialogViewModel(
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
        viewModel = MockBindDialogViewModel(
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
        viewModel = MockBindDialogViewModel(
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
        viewModel = MockBindDialogViewModel(
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
        viewModel = MockBindDialogViewModel(
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

private class MockBindDialogViewModel(state: State) : BindDialogViewModel {
    override val stateFlow: StateFlow<State> = MutableStateFlow(state)
    override fun bind(location: Location) = error("")
}

@Suppress("ConstPropertyName")
internal object BindDialogTags {
    const val root = "BindDialogTags_root"
    const val cancelButton = "BindDialogTags_cancelButton"
    const val bindButton = "BindDialogTags_bindButton"
}

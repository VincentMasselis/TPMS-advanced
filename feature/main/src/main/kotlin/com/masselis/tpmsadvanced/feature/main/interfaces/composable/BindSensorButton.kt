@file:Suppress("NAME_SHADOWING")

package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.BindSensorTags.Button.tag
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.BindSensorTags.Dialog.addToFavoritesButton
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.BindSensorTags.Dialog.cancelButton
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.BindSensorButtonViewModel.State
import com.masselis.tpmsadvanced.feature.main.ioc.InternalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location

@Composable
internal fun BindSensorButton(
    location: Location,
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: BindSensorButtonViewModel = viewModel(
        key = "BindSensorButtonViewModel_${vehicleComponent.vehicle.uuid}_${location}"
    ) {
        (vehicleComponent as InternalVehicleComponent)
            .InternalTyreComponent(location)
            .BindSensorButtonViewModel(createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    BindSensorButton(location, state, viewModel::bind, modifier)
}

@Composable
private fun BindSensorButton(
    location: Location,
    state: State,
    onBind: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var bondRequest by remember { mutableStateOf<State.RequestBond?>(null) }
    Box(modifier = modifier.testTag(tag(location))) {
        when (val state = state) {
            State.Empty -> {}
            is State.RequestBond ->
                IconButton(
                    onClick = { bondRequest = state },
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.link_variant_plus),
                        contentDescription = "Add this sensor to favorites"
                    )
                }
        }
    }
    if (bondRequest != null)
        BindSensorDialog(
            bondRequest = bondRequest!!,
            onBind = { onBind(); bondRequest = null },
            onDismissRequest = { bondRequest = null }
        )
}

@Composable
private fun BindSensorDialog(
    bondRequest: State.RequestBond,
    onBind: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = { Text("Would you mark this sensor as your favorite sensor ?") },
        text = {
            Text(
                when (bondRequest) {
                    is State.RequestBond.NewBinding ->
                        "When a sensor is set as favorite, TPMS Advanced will only display this sensor for this tyre"

                    is State.RequestBond.AlreadyBound ->
                        @Suppress("MaxLineLength")
                        "This sensor will be removed from the car \"${bondRequest.currentVehicle.name}\"" +
                                " and it will be added to the car \"${bondRequest.targetVehicle.name}\""
                }
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                content = { Text("Cancel") },
                modifier = Modifier.testTag(cancelButton)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onBind,
                content = { Text("Add to favorites") },
                modifier = Modifier.testTag(addToFavoritesButton)
            )
        },
        modifier = Modifier.testTag(BindSensorTags.Dialog.root)
    )
}

@Preview
@Composable
private fun BindSensorButtonNewBindingPreview() {
    BindSensorButton(
        location = Location.Wheel(SensorLocation.REAR_LEFT),
        state = State.RequestBond.NewBinding(previewSensor),
        onBind = {}
    )
}

@Preview
@Composable
private fun BindSensorButtonAlreadyBoundPreview() {
    BindSensorButton(
        location = Location.Wheel(SensorLocation.REAR_LEFT),
        state = State.RequestBond.AlreadyBound(
            previewSensor,
            previewVehicle,
            previewVehicle
        ),
        onBind = {}
    )
}

@Suppress("ConstPropertyName")
internal object BindSensorTags {
    object Button {
        fun tag(location: Location): String =
            "bindSensorButton_${location}"
    }

    object Dialog {
        const val root = "BindSensorTags_Dialog_root"
        const val addToFavoritesButton = "BindSensor_Dialog_addToFavoritesTag"
        const val cancelButton = "BindSensor_Dialog_cancel"
    }
}

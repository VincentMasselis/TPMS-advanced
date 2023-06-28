@file:Suppress("NAME_SHADOWING")

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

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
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorTags.Button.tag
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorTags.Dialog.addToFavoritesTag
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorTags.Dialog.cancelTag
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.Location

@Composable
internal fun BindSensorButton(
    location: Location,
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: BindSensorButtonViewModel = viewModel(
        key = "BindSensorButtonViewModel_${vehicleComponent.vehicle.uuid}_${location}"
    ) {
        vehicleComponent.tyreComponent(location)
            .bindSensorButtonViewModelFactory
            .build(createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState(State.Empty)
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
                        contentDescription = null
                    )
                }
        }
    }
    if (bondRequest != null)
        BindSensorDialog(
            bondRequest = bondRequest!!,
            onBind = { viewModel.bind(); bondRequest = null },
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
                modifier = Modifier.testTag(cancelTag)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onBind,
                content = { Text("Add to favorites") },
                modifier = Modifier.testTag(addToFavoritesTag)
            )
        }
    )
}

public object BindSensorTags {
    public object Button {
        public fun tag(location: Location): String =
            "bindSensorButton_${location}"
    }

    public object Dialog {
        public const val addToFavoritesTag: String = "BindSensor_Dialog_addToFavoritesTag"
        public const val cancelTag: String = "BindSensor_Dialog_cancel"
    }
}

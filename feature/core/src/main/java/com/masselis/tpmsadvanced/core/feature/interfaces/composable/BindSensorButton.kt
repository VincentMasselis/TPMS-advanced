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
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel.State
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorDialogViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.SensorLocation

@Composable
internal fun BindSensorButton(
    location: SensorLocation,
    modifier: Modifier = Modifier,
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: BindSensorButtonViewModel = viewModel(
        key = "BindSensorButtonViewModel_${carComponent.hashCode()}_${location.name}"
    ) {
        carComponent.tyreComponent(location).bindSensorButtonViewModelFactory
            .build(createSavedStateHandle())
    }
) {
    var sensorToAdd by remember { mutableStateOf<Sensor?>(null) }
    Box(modifier = modifier) {
        val state by viewModel.stateFlow.collectAsState(State.Empty)
        when (val state = state) {
            State.Empty -> {}
            is State.RequestBond ->
                IconButton(
                    onClick = { sensorToAdd = state.sensor },
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.link_variant_plus),
                        contentDescription = null
                    )
                }
        }
    }
    if (sensorToAdd != null)
        BindSensorDialog(
            sensorToAdd!!,
            onDismissRequest = { sensorToAdd = null }
        )
}

@Composable
private fun BindSensorDialog(
    sensorToAdd: Sensor,
    onDismissRequest: () -> Unit,
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: BindSensorDialogViewModel = viewModel(
        key = "BindSensorDialogViewModel_${carComponent.hashCode()}_${sensorToAdd.id}"
    ) {
        carComponent.bindSensorDialogViewModelFactory
            .build(sensorToAdd, createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    if (state is BindSensorDialogViewModel.State.Loading)
        return
    AlertDialog(
        title = { Text("Would you mark this sensor as your favorite sensor ?") },
        text = {
            Text(
                when (val state = state) {
                    BindSensorDialogViewModel.State.NewSensor ->
                        "When a sensor is set as favorite, TPMS Advanced will only display this sensor for this tyre"
                    is BindSensorDialogViewModel.State.UpdateSensor ->
                        @Suppress("MaxLineLength")
                        "This sensor will be removed from the car \"${state.currentBound.name}\"" +
                                " and it will be added to the car \"${state.newOwner.name}\""
                    BindSensorDialogViewModel.State.Loading -> error("Unreachable state")
                }
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        confirmButton = {
            TextButton(
                onClick = { viewModel.save(); onDismissRequest() },
                content = { Text("Add to favorites") }
            )
        }
    )
}

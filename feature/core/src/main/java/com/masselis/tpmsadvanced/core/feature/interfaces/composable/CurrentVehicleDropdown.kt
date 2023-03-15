@file:Suppress("NAME_SHADOWING")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleTextViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleTextViewModel.State
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleListDropdownViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.delay

@Composable
public fun CurrentVehicleDropdown(
    modifier: Modifier = Modifier,
): Unit = CurrentVehicleDropdown(modifier, viewModel { FeatureCoreComponent.currentVehicleTextViewModel })

@Composable
internal fun CurrentVehicleDropdown(
    modifier: Modifier = Modifier,
    viewModel: CurrentVehicleTextViewModel = viewModel { FeatureCoreComponent.currentVehicleTextViewModel }
) {
    val state by viewModel.stateFlow.collectAsState()
    val vehicle = when (val state = state) {
        State.Loading -> return
        is State.CurrentVehicle -> state.vehicle
    }
    var expanded by remember { mutableStateOf(false) }
    var askNewVehicle by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.menuAnchor()
        ) {
            Text(text = vehicle.name)
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
        VehicleListDropdownMenu(
            isExpanded = expanded,
            onDismissRequest = { expanded = false },
            onNewCurrent = { viewModel.setCurrent(it) },
            onAskNewVehicle = { askNewVehicle = true }
        )
    }
    if (askNewVehicle)
        AddVehicle(
            onDismissRequest = { askNewVehicle = false },
            onVehicleAdd = { viewModel.insert(it) }
        )
}

@Suppress("NAME_SHADOWING")
@Composable
private fun ExposedDropdownMenuBoxScope.VehicleListDropdownMenu(
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onNewCurrent: (Vehicle) -> Unit,
    onAskNewVehicle: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VehicleListDropdownViewModel = viewModel { FeatureCoreComponent.vehicleListDropdownViewModel }
) {
    val state by viewModel.stateFlow.collectAsState()
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.exposedDropdownSize(false)
    ) {
        state.list.forEach { currentVehicle ->
            DropdownMenuItem(
                text = { Text(currentVehicle.name, Modifier.weight(1f)) },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                onClick = {
                    onNewCurrent(currentVehicle)
                    onDismissRequest()
                },
            )
        }
        DropdownMenuItem(
            text = { Text(text = "Add a car", Modifier.weight(1f)) },
            trailingIcon = { Icon(Icons.Filled.AddCircle, contentDescription = null) },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            onClick = { onAskNewVehicle(); onDismissRequest() },
        )
    }
}

@Composable
private fun AddVehicle(
    onDismissRequest: () -> Unit,
    onVehicleAdd: (name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var vehicleName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = { Text(text = "Add a new car") },
        text = {
            OutlinedTextField(
                label = { Text(text = "Car name") },
                value = vehicleName,
                onValueChange = { vehicleName = it },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        confirmButton = {
            TextButton(
                onClick = { onVehicleAdd(vehicleName); onDismissRequest() },
                content = { Text(text = "Add") }
            )
        }
    )
    LaunchedEffect(key1 = Unit) {
        delay(200)
        focusRequester.requestFocus()
    }
}

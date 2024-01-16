package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.AddVehicle.addButton
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.AddVehicle.cancelButton
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.AddVehicle.kindRadio
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.AddVehicle.textField
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntry
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleDropdownViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleDropdownViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.InternalComponent.Companion.CurrentVehicleDropdownViewModel
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay

@Composable
public fun CurrentVehicleDropdown(
    modifier: Modifier = Modifier,
) {
    CurrentVehicleDropdown(
        modifier,
        viewModel { CurrentVehicleDropdownViewModel(createSavedStateHandle()) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CurrentVehicleDropdown(
    modifier: Modifier = Modifier,
    viewModel: CurrentVehicleDropdownViewModel = viewModel {
        CurrentVehicleDropdownViewModel(createSavedStateHandle())
    }
) {
    val vehicles = viewModel.stateFlow.collectAsState().value as? State.Vehicles ?: return
    var expanded by remember { mutableStateOf(false) }
    var askNewVehicle by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.menuAnchor()
        ) {
            Text(text = vehicles.current.name)
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
        VehicleListDropdownMenu(
            isExpanded = expanded,
            vehicleList = vehicles.list.toImmutableList(),
            onDismissRequest = { expanded = false },
            onNewCurrent = { viewModel.setCurrent(it) },
            onAskNewVehicle = { askNewVehicle = true }
        )
    }
    if (askNewVehicle)
        AddVehicle(
            onDismissRequest = { askNewVehicle = false },
            onVehicleAdd = { name, kind -> viewModel.insert(name, kind); askNewVehicle = false }
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.VehicleListDropdownMenu(
    isExpanded: Boolean,
    vehicleList: ImmutableList<Vehicle>,
    onDismissRequest: () -> Unit,
    onNewCurrent: (Vehicle) -> Unit,
    onAskNewVehicle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.exposedDropdownSize(false)
    ) {
        vehicleList.forEach { vehicle ->
            DropdownMenuItem(
                text = { Text(vehicle.name, Modifier.weight(1f)) },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                onClick = {
                    onNewCurrent(vehicle)
                    onDismissRequest()
                },
                modifier = Modifier.testTag(dropdownEntry(vehicle.name))
            )
        }
        DropdownMenuItem(
            text = { Text(text = "Add a vehicle", Modifier.weight(1f)) },
            trailingIcon = { Icon(Icons.Filled.AddCircle, contentDescription = "") },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            onClick = { onAskNewVehicle(); onDismissRequest() },
            modifier = Modifier.testTag(dropdownEntryAddVehicle)
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun AddVehicle(
    onDismissRequest: () -> Unit,
    onVehicleAdd: (name: String, kind: Vehicle.Kind) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var vehicleName by remember { mutableStateOf("") }
    var currentKind by remember { mutableStateOf<Vehicle.Kind?>(null) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.testTag(CurrentVehicleDropdownTags.AddVehicle.root),
        title = { Text(text = "Add a new vehicle") },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                OutlinedTextField(
                    label = { Text(text = "Vehicle name") },
                    value = vehicleName,
                    onValueChange = { vehicleName = it },
                    singleLine = true,
                    modifier = Modifier
                        .testTag(textField)
                        .focusRequester(focusRequester)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Vehicle.Kind.entries.forEach { kind ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .testTag(kindRadio(kind))
                            .padding(8.dp)
                            .selectable(
                                selected = currentKind == kind,
                                onClick = { currentKind = kind },
                                role = Role.RadioButton
                            )
                    ) {
                        RadioButton(
                            selected = currentKind == kind,
                            onClick = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (kind) {
                                Vehicle.Kind.CAR -> "Car"
                                Vehicle.Kind.SINGLE_AXLE_TRAILER -> "Single axle trailer"
                                Vehicle.Kind.MOTORCYCLE -> "Motorcycle"
                                Vehicle.Kind.TADPOLE_THREE_WHEELER -> "Tadpole three wheeler"
                                Vehicle.Kind.DELTA_THREE_WHEELER -> "Delta three wheeler"
                            }
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                content = { Text("Cancel") },
                modifier = Modifier.testTag(cancelButton)
            )
        },
        confirmButton = {
            TextButton(
                enabled = vehicleName.isBlank().not() && currentKind != null,
                onClick = { onVehicleAdd(vehicleName, currentKind!!) },
                content = { Text(text = "Add") },
                modifier = Modifier.testTag(addButton)
            )
        }
    )
    LaunchedEffect(key1 = Unit) {
        delay(200)
        focusRequester.requestFocus()
    }
}

@Suppress("ConstPropertyName")
internal object CurrentVehicleDropdownTags {
    fun dropdownEntry(vehicleName: String) = "dropdown_entry_$vehicleName"
    const val dropdownEntryAddVehicle = "dropdown_item_add_vehicle"

    object AddVehicle {
        const val root = "CurrentVehicleDropdownTags_AddVehicle_root"
        const val textField = "dialog_add_vehicle_text_field"
        fun kindRadio(kind: Vehicle.Kind) = "dialog_add_vehicle_kind_radio_${kind.name}"

        const val addButton = "dialog_add_vehicle_add_button"
        const val cancelButton = "dialog_add_vehicle_cancel_button"

    }
}

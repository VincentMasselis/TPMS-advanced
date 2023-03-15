package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleAlertViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import kotlinx.coroutines.channels.consumeEach

@Suppress("NAME_SHADOWING")
@Composable
internal fun DeleteVehicle(
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: DeleteVehicleViewModel = viewModel(key = "DeleteVehicleViewModel_${vehicleComponent.hashCode()}") {
        vehicleComponent.deleteVehicleViewModel
    }
) {
    val state = viewModel.stateFlow.collectAsState().value
    val vehicle = when (val state = state) {
        DeleteVehicleViewModel.State.Loading -> return
        is DeleteVehicleViewModel.State.DeletableVehicle -> state.vehicle
        is DeleteVehicleViewModel.State.LatestVehicle -> state.vehicle
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            enabled = state is DeleteVehicleViewModel.State.DeletableVehicle,
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { showDeleteDialog = true }
        ) {
            Icon(ImageVector.vectorResource(id = R.drawable.delete_forever_outline), null)
            Spacer(Modifier.width(6.dp))
            Text(text = "Delete \"${vehicle.name}\"")
        }
    }
    if (showDeleteDialog)
        DeleteVehicleDialog({ showDeleteDialog = false })
}

@Composable
private fun DeleteVehicleDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    viewModel: DeleteVehicleAlertViewModel = viewModel(key = "DeleteVehicleAlertViewModel_${vehicleComponent.hashCode()}") {
        vehicleComponent.deleteVehicleAlertViewModel
    }
) {
    val navController = LocalHomeNavController.current
    val state by viewModel.stateFlow.collectAsState()
    val vehicleState = when (state) {
        DeleteVehicleAlertViewModel.State.Loading -> return
        is DeleteVehicleAlertViewModel.State.Vehicle -> state as DeleteVehicleAlertViewModel.State.Vehicle
    }
    AlertDialog(
        text = {
            Text("Do you really want to delete the car \"${vehicleState.vehicle.name}\" ?\nThis action cannot be undone !")
        },
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = { viewModel.delete() }) { Text("Delete \"${vehicleState.vehicle.name}\"") } },
        modifier = modifier
    )
    LaunchedEffect("EVENTS") {
        viewModel.eventChannel.consumeEach {
            when (it) {
                DeleteVehicleAlertViewModel.Event.Leave -> navController.popBackStack()
            }
        }
    }
}

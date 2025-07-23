package com.masselis.tpmsadvanced.feature.main.interfaces.composable

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.DeleteVehicleButtonTags.Button.tag
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.DeleteVehicleButtonTags.Dialog.cancel
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.DeleteVehicleButtonTags.Dialog.delete
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DeleteVehicleViewModel.Event
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DeleteVehicleViewModel.State
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.InternalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.InternalVehicleComponent.Companion.viewModel

@Composable
internal fun DeleteVehicleButton(
    modifier: Modifier = Modifier,
    vehicleComponent: InternalVehicleComponent = LocalInternalVehicleComponent.current,
    viewModel: DeleteVehicleViewModel = vehicleComponent.viewModel { it.DeleteVehicleViewModel() }
) {
    val navController = LocalHomeNavController.current
    val state by viewModel.stateFlow.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            enabled = state is State.DeletableVehicle,
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .testTag(tag),
        ) {
            Icon(ImageVector.vectorResource(id = R.drawable.delete_forever_outline), null)
            Spacer(Modifier.width(6.dp))
            Text(text = "Delete \"${state.vehicle.name}\"")
        }
    }
    if (showDeleteDialog)
        DeleteVehicleDialog(
            vehicle = state.vehicle,
            onDismissRequest = { showDeleteDialog = false },
            onDelete = { viewModel.delete(); showDeleteDialog = false; }
        )
    LaunchedEffect(viewModel) {
        for (event in viewModel.eventChannel) {
            when (event) {
                Event.Leave -> navController.popBackStack()
            }
        }
    }
}

@Composable
private fun DeleteVehicleDialog(
    vehicle: Vehicle,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        text = {
            Text("Do you really want to delete the car \"${vehicle.name}\" ?\nThis action cannot be undone !")
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                content = { Text("Cancel") },
                modifier = Modifier.testTag(cancel)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDelete,
                content = { Text("Delete \"${vehicle.name}\"") },
                modifier = Modifier.testTag(delete)
            )
        },
        modifier = modifier.testTag(DeleteVehicleButtonTags.Dialog.root)
    )
}

@Suppress("ConstPropertyName")
internal object DeleteVehicleButtonTags {
    object Button {
        const val tag = "DeleteVehicleButton_Button_tag"
    }

    object Dialog {
        const val root = "DeleteVehicleButtonTags_Dialog_root"
        const val cancel = "DeleteVehicleButton_Dialog_cancel"
        const val delete = "DeleteVehicleButton_Dialog_delete"
    }
}

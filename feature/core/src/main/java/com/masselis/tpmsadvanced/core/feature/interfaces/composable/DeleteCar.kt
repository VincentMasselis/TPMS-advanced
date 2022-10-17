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
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteCarAlertViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteCarViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import com.masselis.tpmsadvanced.core.ui.observeWithLifecycle

@Suppress("NAME_SHADOWING")
@Composable
internal fun DeleteCar(
    modifier: Modifier = Modifier,
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: DeleteCarViewModel = viewModel(key = "DeleteCarViewModel_${carComponent.hashCode()}") {
        carComponent.deleteCarViewModel
    }
) {
    val state = viewModel.stateFlow.collectAsState().value
    val car = when (val state = state) {
        DeleteCarViewModel.State.Loading -> return
        is DeleteCarViewModel.State.DeletableCar -> state.car
        is DeleteCarViewModel.State.LatestCar -> state.car
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            enabled = state is DeleteCarViewModel.State.DeletableCar,
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { showDeleteDialog = true }
        ) {
            Icon(ImageVector.vectorResource(id = R.drawable.delete_forever_outline), null)
            Spacer(Modifier.width(6.dp))
            Text(text = "Delete \"${car.name}\"")
        }
    }
    if (showDeleteDialog)
        DeleteCarAlert({ showDeleteDialog = false })
}

@Composable
private fun DeleteCarAlert(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: DeleteCarAlertViewModel = viewModel(key = "DeleteCarAlertViewModel_${carComponent.hashCode()}") {
        carComponent.deleteCarAlertViewModel
    }
) {
    val navController = LocalHomeNavController.current
    viewModel.eventFlow.observeWithLifecycle {
        when (it) {
            DeleteCarAlertViewModel.Event.Leave -> navController.popBackStack()
        }
    }
    val state by viewModel.stateFlow.collectAsState()
    val carState = when (state) {
        DeleteCarAlertViewModel.State.Loading -> return
        is DeleteCarAlertViewModel.State.Car -> state as DeleteCarAlertViewModel.State.Car
    }
    AlertDialog(
        text = {
            Text("Do you really want to delete the car \"${carState.car.name}\" ?\nThis action cannot be undone !")
        },
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = { viewModel.delete() }) { Text("Delete \"${carState.car.name}\"") } },
        modifier = modifier
    )
}
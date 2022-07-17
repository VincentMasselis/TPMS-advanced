package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.R
import com.masselis.tpmsadvanced.interfaces.viewmodel.SensorFavouriteViewModel
import com.masselis.tpmsadvanced.model.TyreLocation

@Composable
fun FavouriteButton(
    tyreLocation: TyreLocation,
    modifier: Modifier = Modifier,
    viewModel: SensorFavouriteViewModel = viewModel(key = "SensorFavouriteViewModel_${tyreLocation.name}") {
        tyreLocation.component.sensorFavouriteViewModelFactory.build(createSavedStateHandle())
    }
) {
    var isDialogDisplayed by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        val state by viewModel.stateFlow.collectAsState()
        when (state) {
            SensorFavouriteViewModel.State.Empty -> {}
            is SensorFavouriteViewModel.State.RequestBond ->
                IconButton(
                    onClick = { isDialogDisplayed = true },
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.link_variant_plus),
                        contentDescription = null
                    )
                }
        }
    }
    if (isDialogDisplayed)
        FavouriteAlertDialog(
            onAccept = { viewModel.save() },
            onDismissRequest = { isDialogDisplayed = false }
        )
}

@Composable
private fun FavouriteAlertDialog(
    onAccept: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        title = { Text("Would you mark this sensor as your favorite sensor ?") },
        text = { Text("When a sensor is set as favorite, TPMS Advanced will stop to display other sensors") },
        onDismissRequest = onDismissRequest,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        confirmButton = {
            TextButton(onClick = {
                onAccept()
                onDismissRequest()
            }) {
                Text("Add to favorites")
            }
        }
    )
}
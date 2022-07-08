package com.masselis.tpmsadvanced.interfaces.composable

import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.interfaces.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.CameraPreconditionsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.QRCodeViewModel

@Suppress("NAME_SHADOWING")
@Composable
fun QrCodeScan(
    modifier: Modifier = Modifier,
    viewModel: CameraPreconditionsViewModel = viewModel {
        mainComponent.cameraPreconditionsViewModel.build(createSavedStateHandle())
    }
) {
    OnLifecycleEvent { _, event ->
        if (event == Lifecycle.Event.ON_RESUME)
            viewModel.trigger()
    }
    val state by viewModel.stateFlow.collectAsState()
    when (val state = state) {
        CameraPreconditionsViewModel.State.Loading -> {}
        is CameraPreconditionsViewModel.State.MissingPermission -> MissingPermission(
            "TPMS Advanced need you to approve a permission to scan the QR Code",
            "Failed to obtain permission, please update this in the app's system settings to continue",
            listOf(state.permission),
            modifier = modifier,
        ) { viewModel.trigger() }
        CameraPreconditionsViewModel.State.Ready -> Preview(
            modifier = modifier
        )
    }
}

@Suppress("NAME_SHADOWING")
@Composable
private fun Preview(
    cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA,
    modifier: Modifier = Modifier,
) {
    val controller = LocalContext.current
        .let { context ->
            remember {
                LifecycleCameraController(context).apply { this.cameraSelector = cameraSelector }
            }
        }
        .also { controller ->
            LocalLifecycleOwner.current.also { lifecycleOwner ->
                DisposableEffect(controller) {
                    controller.bindToLifecycle(lifecycleOwner)
                    onDispose { controller.unbind() }
                }
            }
        }

    AndroidView(
        { context -> PreviewView(context).apply { this.controller = controller } },
        modifier = modifier
    )

    val viewModel = remember { mainComponent.qrCodeViewModel.build(controller) }
    val state by viewModel.stateFlow.collectAsState()
    when (val state = state) {
        QRCodeViewModel.State.Scanning -> {}

        is QRCodeViewModel.State.AskFavourites ->
            AlertDialog(
                text = { Text(text = "Would you add theses sensors as your favourite sensors ?") },
                onDismissRequest = { viewModel.scanAgain() },
                confirmButton = {
                    TextButton(onClick = { viewModel.addToFavourites(state.sensorIds) }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.scanAgain() }) {
                        Text(text = "Cancel")
                    }
                }
            )

        QRCodeViewModel.State.Leave ->
            HomeNavController.current.popBackStack()

    }
}
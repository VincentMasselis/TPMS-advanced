package com.masselis.tpmsadvanced.qrcode.interfaces

import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import com.masselis.tpmsadvanced.core.ui.MissingPermission
import com.masselis.tpmsadvanced.qrcode.R
import com.masselis.tpmsadvanced.qrcode.ioc.FeatureQrCodeComponent

@Composable
public fun QrCodeScan(
    modifier: Modifier = Modifier,
): Unit = QrCodeScan(
    modifier,
    viewModel { FeatureQrCodeComponent.cameraPreconditionsViewModel.build(createSavedStateHandle()) },
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun QrCodeScan(
    modifier: Modifier = Modifier,
    viewModel: CameraPreconditionsViewModel = viewModel {
        FeatureQrCodeComponent.cameraPreconditionsViewModel.build(createSavedStateHandle())
    },
) {
    val permissionState = rememberMultiplePermissionsState(listOf(viewModel.requiredPermissions()))
    when {

        permissionState.allPermissionsGranted.not() -> MissingPermission(
            text = "TPMS Advanced need you to approve a permission to scan the QR Code",
            refusedText = "Failed to obtain permission, please update this in the app's system settings to continue",
            permissionState = permissionState,
            modifier = modifier,
        )

        else -> Preview(
            modifier = modifier
        )
    }
}

@Suppress("NAME_SHADOWING")
@Composable
private fun Preview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA,
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

    Box(modifier) {
        AndroidView(
            { context -> PreviewView(context).apply { this.controller = controller } },
            Modifier.fillMaxSize()
        )
        QrCodeOverlay(Modifier.fillMaxSize())
    }

    val viewModel = remember { FeatureQrCodeComponent.qrCodeViewModel.build(controller) }
    val state by viewModel.stateFlow.collectAsState()
    when (val state = state) {
        QRCodeViewModel.State.Scanning -> {}

        is QRCodeViewModel.State.AskForBinding ->
            AlertDialog(
                text = { Text(text = "Would you add theses sensors as your favourite sensors ?") },
                onDismissRequest = { viewModel.scanAgain() },
                confirmButton = {
                    TextButton(onClick = { viewModel.bindSensors(state.sensorMap) }) {
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
            LocalHomeNavController.current.popBackStack()

    }
}

@Composable
private fun QrCodeOverlay(
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Image(
            painterResource(id = R.drawable.qr_core_sample),
            null,
            Modifier
                .fillMaxWidth(0.7f)
                .alpha(.4f)
                .align(Alignment.Center)
        )
    }
}

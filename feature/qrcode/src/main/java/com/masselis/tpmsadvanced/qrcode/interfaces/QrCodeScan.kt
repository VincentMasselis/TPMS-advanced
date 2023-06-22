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
import androidx.compose.runtime.LaunchedEffect
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
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.qrcode.R
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel.Event
import com.masselis.tpmsadvanced.qrcode.interfaces.QRCodeViewModel.State
import com.masselis.tpmsadvanced.qrcode.ioc.FeatureQrCodeComponent
import kotlinx.coroutines.channels.consumeEach

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
        State.Scanning -> {}

        is State.AskForBinding -> BindingAlert(
            state = state,
            onDismissRequest = viewModel::scanAgain,
            onBind = viewModel::bindSensors
        )
    }

    val navController = LocalHomeNavController.current
    LaunchedEffect(viewModel) {
        viewModel.receiveChannel.consumeEach {
            when (it) {
                Event.Leave -> navController.popBackStack()
            }
        }
    }
}

@Suppress("CyclomaticComplexMethod", "LongMethod")
@Composable
private fun BindingAlert(
    state: State.AskForBinding,
    onDismissRequest: () -> Unit,
    onBind: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = StringBuilder("Would you add theses sensors as your favourite sensors ?")
                    .apply {
                        when (state) {

                            is State.AskForBinding.Compatible -> {}

                            is State.AskForBinding.Missing -> {
                                append("\n\n⚠️ Filled QR Code doesn't contains sensors dedicated to ")
                                state.localisations.forEachIndexed { index, location ->
                                    append("the ")
                                    when (location) {
                                        is Vehicle.Kind.Location.Wheel -> {
                                            append(
                                                when (location.location) {
                                                    SensorLocation.FRONT_LEFT -> "front left"
                                                    SensorLocation.FRONT_RIGHT -> "front right"
                                                    SensorLocation.REAR_LEFT -> "rear left"
                                                    SensorLocation.REAR_RIGHT -> "rear right"
                                                }
                                            )
                                            append(" wheel")
                                        }

                                        is Vehicle.Kind.Location.Axle -> {
                                            append(
                                                when (location.axle) {
                                                    SensorLocation.Axle.FRONT -> "front"
                                                    SensorLocation.Axle.REAR -> "rear"
                                                }
                                            )
                                            append(" axle")
                                        }

                                        is Vehicle.Kind.Location.Side -> {
                                            append(
                                                when (location.side) {
                                                    SensorLocation.Side.LEFT -> "left"
                                                    SensorLocation.Side.RIGHT -> "right"
                                                }
                                            )
                                            append(" side")
                                        }
                                    }
                                    append(
                                        when (index) {
                                            state.localisations.size - 1 -> "."
                                            state.localisations.size - 2 -> " and "
                                            else -> ", "
                                        }
                                    )
                                }
                            }
                        }
                    }
                    .toString()
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onBind) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }
    )
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

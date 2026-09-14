package com.masselis.tpmsadvanced.feature.qrcode.interfaces

import android.Manifest.permission.CAMERA
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import com.masselis.tpmsadvanced.core.ui.MissingPermission
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.appendLoc
import com.masselis.tpmsadvanced.feature.qrcode.R
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.QRCodeViewModel.Event
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.QRCodeViewModel.State
import com.masselis.tpmsadvanced.feature.qrcode.ioc.Bindings.Companion.QrCodeViewModel
import androidx.compose.foundation.layout.Column
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalPermissionsApi::class)
@Composable
public fun QrCodeScan(
    snackbarHostState: SnackbarHostState,
    openUnlocatedSensorBinding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissionState = rememberMultiplePermissionsState(listOf(CAMERA))
    when {
        permissionState.allPermissionsGranted.not() -> MissingPermission(
            text = "TPMS Advanced need you to approve a permission to scan the QR Code",
            refusedText = "Failed to obtain permission, please update this in the app's system settings to continue",
            permissionState = permissionState,
            modifier = modifier,
        )

        else -> Preview(
            snackbarHostState = snackbarHostState,
            openUnlocatedSensorBinding = openUnlocatedSensorBinding,
            modifier = modifier,
        )
    }
}

@Suppress("NAME_SHADOWING")
@Composable
private fun Preview(
    snackbarHostState: SnackbarHostState,
    openUnlocatedSensorBinding: () -> Unit,
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

	var showSensorIdEntry by remember { mutableStateOf(false) }
	
	Box(modifier) {
		AndroidView(
			{ context -> PreviewView(context).apply { this.controller = controller } },
			Modifier.fillMaxSize()
		)

		QrCodeOverlay(Modifier.fillMaxSize())

		TextButton(
			onClick = { showSensorIdEntry = true },
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(16.dp)
		) {
			Text("Enter sensor ID")
		}
	}

    val viewModel = remember(controller) { QrCodeViewModel(controller) }
	
	if (showSensorIdEntry) {
		EnterSensorIdDialog(
			onDismissRequest = {
				showSensorIdEntry = false
			},
			onSubmit = { sensorId ->
				viewModel.enterSensorId(sensorId)
				showSensorIdEntry = false
			}
		)
	}

    val navController = LocalHomeNavController.current
    val state by viewModel.stateFlow.collectAsState()
	when (val state = state) {
		State.Scanning -> {}

		is State.AskForBinding -> BindingAlert(
			state = state,
			onDismissRequest = viewModel::scanAgain,
			onBind = viewModel::bindSensors
		)

		is State.AskForSingleSensorBinding -> SingleSensorBindingAlert(
			state = state,
			onDismissRequest = viewModel::scanAgain,
			onBind = viewModel::bindSingleSensor,
		)

		is State.Error -> ErrorAlert(
			state = state,
			onDismissRequest = viewModel::scanAgain,
			openUnlocatedSensorBinding = openUnlocatedSensorBinding,
		)
	}

    LaunchedEffect(viewModel) {
        for (event in viewModel.eventChannel) {
            when (event) {
                Event.Leave -> navController.popBackStack()

                Event.LeaveBecauseCameraUnavailable -> {
                    snackbarHostState.showSnackbar("Your device should to have a camera to continue")
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
private fun EnterSensorIdDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var sensorId by remember { mutableStateOf("") }

    val isValid = sensorId.matches(
        Regex("^[0-9A-F]{6}$")
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Enter sensor ID")
        },
        text = {
            Column {
                Text("Enter the 6-character hexadecimal ID printed on the sensor.")

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sensorId,
                    onValueChange = { value ->
                        sensorId = value
                            .uppercase()
                            .filter { char ->
                                char in '0'..'9' || char in 'A'..'F'
                            }
                            .take(6)
                    },
                    singleLine = true,
                    label = {
                        Text("Sensor ID")
                    },
                    placeholder = {
                        Text("002D56")
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = {
                    onSubmit(sensorId)
                }
            ) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
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
                                state.locations.forEachIndexed { index, location ->
                                    append("the ")
                                    appendLoc(location)
                                    append(
                                        when (index) {
                                            state.locations.size - 1 -> "."
                                            state.locations.size - 2 -> " and "
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
private fun SingleSensorBindingAlert(
    state: State.AskForSingleSensorBinding,
    onDismissRequest: () -> Unit,
    onBind: (Vehicle.Kind.Location) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Bind sensor")
        },
        text = {
            Column {
                Text(
                    "Sensor ID: ${
                        "%02X%02X%02X".format(
                            state.sensorId and 0xFF,
                            (state.sensorId shr 8) and 0xFF,
                            (state.sensorId shr 16) and 0xFF,
                        )
                    }"
                )

                Text("Choose where to bind this sensor:")

                state.locations.forEach { location ->
                    TextButton(
                        onClick = { onBind(location) }
                    ) {
                        Text(
                            StringBuilder()
                                .appendLoc(location)
                                .toString()
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Suppress("MaxLineLength")
@Composable
private fun ErrorAlert(
    state: State.Error,
    onDismissRequest: () -> Unit,
    openUnlocatedSensorBinding: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = StringBuilder("Detected inconsistency with the QR Code")
                    .apply {
                        when (state) {
                            is State.Error.DuplicateWheelLocation -> {
                                append("\n\nFilled QR Code contains different sensors associated to the same wheel, ")
                                if (state.wheels.size == 1) append("duplication:")
                                else append("duplications:")

                                state.wheels.forEach { location ->
                                    append("\n   - ")
                                    appendLoc(location)
                                }
                            }

                            is State.Error.DuplicateId -> {
                                append("\n\nFilled QR Code contains the same sensor id multiple times")
                            }
                        }
                    }
                    .toString()
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "OK")
            }
        },
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

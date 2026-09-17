package com.masselis.tpmsadvanced.feature.background.interfaces.ui

import android.Manifest.permission.BLUETOOTH_SCAN
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.feature.background.R
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel.Event
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel.State
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundComponent
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundComponent.Companion.BackgroundViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.LocalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent.Factory.Companion.key

@Composable
public fun BackgroundIconButton(
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
) {
    BackgroundIconButton(
        modifier = modifier,
        keyed = vehicleComponent.key(),
        component = BackgroundComponent(vehicleComponent),
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Suppress("CyclomaticComplexMethod", "LongMethod")
@Composable
internal fun BackgroundIconButton(
    keyed: Map<String, String>,
    component: BackgroundComponent,
    modifier: Modifier = Modifier,
    viewModel: BackgroundViewModel = component.viewModel(keyed) { it.BackgroundViewModel() }
) {
    val state by viewModel.stateFlow.collectAsState()
    val activity = LocalActivity.current
    val permissions = remember {
        mutableListOf<String>()
            .apply { if (SDK_INT >= TIRAMISU) add(POST_NOTIFICATIONS) }
            .apply { if (SDK_INT >= UPSIDE_DOWN_CAKE) add(BLUETOOTH_SCAN) }
            .toList()
    }
    val permissionState = rememberMultiplePermissionsState(permissions)
    val powerManager = activity?.getSystemService<PowerManager>()
    var showBatteryOptimizationAlert by remember { mutableStateOf(false) }
    var showNotificationPermissionAlert by remember { mutableStateOf(false) }
    var showReadyToMonitorAlert by remember { mutableStateOf(false) }
    // Tracks the multi-step "enable monitoring" journey across the trip to Settings and back, so
    // each precondition that becomes satisfied automatically advances to the next one instead of
    // requiring the user to tap the button again after every fix.
    var flowInProgress by remember { mutableStateOf(false) }
    var remediationWasNeeded by remember { mutableStateOf(false) }

    // No FLAG_ACTIVITY_NEW_TASK: we always launch from a live Activity, so Settings can push
    // onto our own task's back stack. Adding it here let a second RootActivity instance spawn
    // when the flow bounced to Settings twice in a row (notifications, then battery).
    fun openAppSettings() = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply { addCategory(Intent.CATEGORY_DEFAULT) }
        .apply { data = "package:${activity!!.packageName}".toUri() }
        .apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }
        .apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
        .also { activity!!.startActivity(it) }

    fun openNotificationSettings() = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .apply { putExtra(Settings.EXTRA_APP_PACKAGE, activity!!.packageName) }
        .apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }
        .apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
        .also { activity!!.startActivity(it) }

    // Requesting via our own launcher (rather than permissionState.launchMultiplePermissionRequest())
    // gives us a completion callback: shouldShowRationale can't tell "never asked" apart from
    // "permanently denied", but this fires exactly once the request is settled either way, letting
    // us react to "still not granted" uniformly regardless of whether the system dialog appeared.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }.not()) showNotificationPermissionAlert = true
    }

    fun proceedEnablingFlow() {
        when {
            permissionState.allPermissionsGranted.not() -> {
                remediationWasNeeded = true
                permissionLauncher.launch(permissions.toTypedArray())
            }

            // Unrestricted battery usage isn't a runtime permission, so it can't be requested
            // directly; explain why it's needed before sending the user to the app's settings
            // page. Without this, the foreground service can be killed shortly after starting.
            powerManager?.isIgnoringBatteryOptimizations(activity!!.packageName) == false -> {
                remediationWasNeeded = true
                showBatteryOptimizationAlert = true
            }

            // Only interrupt with a confirmation when the user actually had to go fix something;
            // otherwise monitoring starts directly, as it always did for an already-configured app.
            remediationWasNeeded -> showReadyToMonitorAlert = true

            else -> {
                flowInProgress = false
                viewModel.monitor()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // Resuming means the user came back from Settings (or from the system permission
            // dialog); re-check where the flow stands, unless one of our own alerts is already
            // waiting for an explicit answer.
            val noAlertShowing = showNotificationPermissionAlert.not() &&
                    showBatteryOptimizationAlert.not() &&
                    showReadyToMonitorAlert.not()
            if (event == ON_RESUME && flowInProgress && noAlertShowing) {
                proceedEnablingFlow()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AnimatedContent(state) { state ->
        when (state) {
            State.Idle -> IconButton(
                onClick = {
                    flowInProgress = true
                    remediationWasNeeded = false
                    proceedEnablingFlow()
                },
                modifier.testTag("put_in_background_button")
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.format_vertical_align_center),
                    contentDescription = "Enable background monitoring",
                )
            }

            State.Monitoring -> IconButton(
                onClick = viewModel::disableMonitoring,
                modifier.testTag("cancel_background_button")
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.cancel),
                    contentDescription = "Cancel background monitoring",
                )
            }
        }
    }
    LaunchedEffect(viewModel.eventChannel) {
        for (event in viewModel.eventChannel) {
            when (event) {
                Event.FinishActivity -> activity!!.finish()
            }
        }
    }
    if (showBatteryOptimizationAlert) {
        BatteryOptimizationAlert(
            onDismissRequest = {
                showBatteryOptimizationAlert = false
                flowInProgress = false
            },
            onConfirm = {
                showBatteryOptimizationAlert = false
                openAppSettings()
            }
        )
    }
    if (showNotificationPermissionAlert) {
        NotificationPermissionAlert(
            onDismissRequest = {
                showNotificationPermissionAlert = false
                flowInProgress = false
            },
            onConfirm = {
                showNotificationPermissionAlert = false
                openNotificationSettings()
            }
        )
    }
    if (showReadyToMonitorAlert) {
        ReadyToMonitorAlert(
            onConfirm = {
                showReadyToMonitorAlert = false
                flowInProgress = false
                viewModel.monitor()
            }
        )
    }
}

@Composable
private fun BatteryOptimizationAlert(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = "To keep monitoring your tyres reliably in the background, this app " +
                        "needs to be exempted from battery optimization (sometimes labelled " +
                        "\"unrestricted\" battery usage in your device's settings). Without " +
                        "it, the system is likely to stop background monitoring shortly after " +
                        "it starts."
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Open settings")
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
private fun NotificationPermissionAlert(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = "This app needs notifications enabled to alert you when a tyre goes " +
                        "out of range while background monitoring is running. Please enable " +
                        "notifications for this app to continue."
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Open settings")
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
private fun ReadyToMonitorAlert(
    onConfirm: () -> Unit,
) {
    AlertDialog(
        text = { Text(text = "All permissions granted, starting monitoring") },
        onDismissRequest = onConfirm,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "OK")
            }
        }
    )
}

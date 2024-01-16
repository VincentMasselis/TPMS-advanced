package com.masselis.tpmsadvanced.feature.background.interfaces.ui

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.core.net.toUri
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.LocalVehicleComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.feature.background.R
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.ManualBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.ManualBackgroundViewModel.State
import com.masselis.tpmsadvanced.feature.background.ioc.InternalComponent.Companion.ManualBackgroundViewModel

@Composable
public fun ManualBackgroundIconButton(
    modifier: Modifier = Modifier,
    component: VehicleComponent = LocalVehicleComponent.current,
) {
    ManualBackgroundIconButton(
        modifier = modifier,
        component = component,
        viewModel = viewModel(key = "ManualBackgroundViewModel_${component.vehicle.uuid}") {
            ManualBackgroundViewModel(component.vehicle, createSavedStateHandle())
        }
    )
}

@Composable
internal fun ManualBackgroundIconButton(
    modifier: Modifier = Modifier,
    component: VehicleComponent = LocalVehicleComponent.current,
    viewModel: ManualBackgroundViewModel = viewModel(key = "ManualBackgroundViewModel_${component.vehicle.uuid}") {
        ManualBackgroundViewModel(component.vehicle, createSavedStateHandle())
    }
) {
    val state by viewModel.stateFlow.collectAsState()
    val activity = LocalContext.current as Activity
    var hasRefusedPermission by remember { mutableStateOf(false) }

    fun monitor() {
        viewModel.monitor()
        activity.finish()
    }

    val launcher = rememberLauncherForActivityResult(RequestPermission()) { isGrant ->
        if (isGrant) monitor()
        else hasRefusedPermission = true
    }
    // Only create an open animation when the state is different from Loading. If AnimatedVisibility
    // is called with Loading, reaching Idle or Monitoring for the first time will play an animation
    // while I only want to animate when the button is tap.
    if (state != State.Loading) {
        AnimatedVisibility(state is State.Idle) {
            IconButton(
                onClick = {
                    when {
                        hasRefusedPermission ->
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .apply { addCategory(Intent.CATEGORY_DEFAULT) }
                                .apply { data = "package:${activity.packageName}".toUri() }
                                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                .apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }
                                .apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
                                .also { activity.startActivity(it) }

                        viewModel.isPermissionGrant().not() ->
                            launcher.launch(viewModel.requiredPermission())

                        else -> monitor()
                    }
                },
                modifier.testTag("put_in_background_button")
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.format_vertical_align_center),
                    contentDescription = "Enable background monitoring",
                )
            }
        }
        AnimatedVisibility(state is State.Monitoring) {
            IconButton(
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
}

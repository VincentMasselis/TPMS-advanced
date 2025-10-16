package com.masselis.tpmsadvanced.feature.background.interfaces.ui

import android.Manifest.permission.BLUETOOTH_SCAN
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.feature.background.R
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel.Event
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.BackgroundViewModel.State
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundComponent
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.LocalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent.Companion.key

@Composable
public fun BackgroundIconButton(
    modifier: Modifier = Modifier,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
) {
    BackgroundIconButton(
        modifier = modifier,
        keyed = vehicleComponent.key(),
        component = BackgroundComponent.Factory(vehicleComponent),
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Suppress("CyclomaticComplexMethod")
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

    AnimatedContent(state) { state ->
        when (state) {
            State.Idle -> IconButton(
                onClick = {
                    when {
                        permissionState.allPermissionsGranted -> viewModel.monitor()

                        permissionState.shouldShowRationale ->
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .apply { addCategory(Intent.CATEGORY_DEFAULT) }
                                .apply { data = "package:${activity!!.packageName}".toUri() }
                                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                .apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }
                                .apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
                                .also { activity!!.startActivity(it) }

                        permissionState.allPermissionsGranted.not() ->
                            permissionState.launchMultiplePermissionRequest()
                    }
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
}

package com.masselis.tpmsadvanced.feature.background.interfaces.ui

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.AutomaticBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.AutomaticBackgroundViewModel.State
import com.masselis.tpmsadvanced.feature.background.ioc.InternalComponent.Companion.AutomaticBackgroundViewModel

@Composable
public fun AutomaticBackgroundSettings(
    component: VehicleComponent,
    modifier: Modifier = Modifier
) {
    AutomaticBackgroundSettings(
        component,
        modifier,
        viewModel(key = "BackgroundViewModel_${component.vehicle.uuid}") {
            AutomaticBackgroundViewModel(component.vehicle)
        }
    )
}

@Suppress("CyclomaticComplexMethod")
@Composable
internal fun AutomaticBackgroundSettings(
    component: VehicleComponent,
    modifier: Modifier = Modifier,
    viewModel: AutomaticBackgroundViewModel = viewModel(key = "BackgroundViewModel_${component.vehicle.uuid}") {
        AutomaticBackgroundViewModel(component.vehicle)
    }
) {
    val activity = LocalContext.current as Activity
    var hasRefusedPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(RequestPermission()) { isGrant ->
        if (isGrant) viewModel.monitor()
        else hasRefusedPermission = true
    }
    val state by viewModel.stateFlow.collectAsState()
    Row(modifier) {
        Text(
            text = "Monitor ${component.vehicle.name} when the app is in background",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Spacer(
            modifier = Modifier.width(16.dp)
        )
        Switch(
            when (state) {
                State.MonitorDisabled -> false
                State.MonitorEnabled -> true
            }, onCheckedChange = { checked ->
                when {
                    checked.not() ->
                        viewModel.disable()

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

                    checked ->
                        viewModel.monitor()

                    else -> error("Unreachable state")
                }
            }
        )
    }
}

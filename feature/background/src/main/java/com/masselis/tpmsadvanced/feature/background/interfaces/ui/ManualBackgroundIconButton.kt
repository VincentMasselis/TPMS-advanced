package com.masselis.tpmsadvanced.feature.background.interfaces.ui

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
import com.masselis.tpmsadvanced.core.feature.background.R
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.ManualBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent

@Composable
public fun ManualBackgroundIconButton(
    modifier: Modifier = Modifier
) {
    ManualBackgroundIconButton(
        modifier,
        viewModel {
            FeatureBackgroundComponent
                .manualBackgroundViewModel
                .build(createSavedStateHandle())
        }
    )
}

@Composable
internal fun ManualBackgroundIconButton(
    modifier: Modifier = Modifier,
    viewModel: ManualBackgroundViewModel = viewModel {
        FeatureBackgroundComponent.manualBackgroundViewModel.build(createSavedStateHandle())
    }
) {
    val activity = LocalContext.current as Activity
    var hasRefusedPermission by remember { mutableStateOf(false) }

    fun monitor() {
        viewModel.monitor()
        activity.finish()
    }

    val launcher =
        rememberLauncherForActivityResult(RequestPermission()) { isGrant ->
            if (isGrant) monitor()
            else hasRefusedPermission = true
        }
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
            contentDescription = null,
        )
    }
}
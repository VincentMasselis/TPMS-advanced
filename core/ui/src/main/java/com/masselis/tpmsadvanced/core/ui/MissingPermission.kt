package com.masselis.tpmsadvanced.core.ui

import android.app.Activity
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
public fun MissingPermission(
    text: String,
    refusedText: String,
    permissionState: MultiplePermissionsState,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as Activity
    var hasRefusedGrant by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (hasRefusedGrant) refusedText
            else text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 8.dp)
        )
        FilledTonalButton(onClick = {
            if (hasRefusedGrant)
                Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                    .apply { addCategory(CATEGORY_DEFAULT) }
                    .apply { data = "package:${activity.packageName}".toUri() }
                    .apply { addFlags(FLAG_ACTIVITY_NEW_TASK) }
                    .apply { addFlags(FLAG_ACTIVITY_NO_HISTORY) }
                    .apply { addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
                    .also { activity.startActivity(it) }
            else {
                permissionState.launchMultiplePermissionRequest()
                hasRefusedGrant = true
            }
        }) {
            Text(
                if (hasRefusedGrant) "Open settings"
                else "Grant permission"
            )
        }
    }
}

package com.masselis.tpmsadvanced.uicommon

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@Composable
fun MissingPermission(
    text: String,
    refusedText: String,
    missingPermissions: List<String>,
    modifier: Modifier = Modifier,
    trigger: () -> Unit,
) {
    val activity = LocalContext.current as Activity
    var hasRefusedGrant by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(RequestMultiplePermissions()) { granted ->
        if (granted.values.any { it.not() }) hasRefusedGrant = true
        else trigger()
    }
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
                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .apply { addCategory(Intent.CATEGORY_DEFAULT) }
                    .apply { data = "package:${activity.packageName}".toUri() }
                    .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                    .apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }
                    .apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
                    .also { activity.startActivity(it) }
            else
                launcher.launch(missingPermissions.toTypedArray())
        }) {
            Text(
                if (hasRefusedGrant) "Open settings"
                else "Grant permission"
            )
        }
    }
}
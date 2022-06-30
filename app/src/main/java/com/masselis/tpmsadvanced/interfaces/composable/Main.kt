@file:OptIn(ExperimentalMaterial3Api::class)

package com.masselis.tpmsadvanced.interfaces.composable

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.Intent.*
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import com.masselis.tpmsadvanced.interfaces.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.PreconditionsViewModel.State
import com.masselis.tpmsadvanced.interfaces.viewmodel.utils.savedStateViewModel
import com.masselis.tpmsadvanced.mock.mocks

@Suppress("NAME_SHADOWING")
@Composable
fun Main(
    viewModel: PreconditionsViewModel = savedStateViewModel {
        mainComponent.preconditionsViewModel.build(it)
    }
) {
    OnLifecycleEvent { _, event ->
        if (event == Lifecycle.Event.ON_RESUME)
            viewModel.trigger()
    }
    val state by viewModel.stateFlow.collectAsState()
    Scaffold {
        when (val state = state) {
            State.Loading -> Text("Loading")
            State.BluetoothChipTurnedOff -> ChipIsOff(modifier = Modifier.fillMaxSize())
            is State.MissingPermission -> MissingPermission(
                modifier = Modifier.fillMaxSize(),
                state
            ) { viewModel.trigger() }
            State.Ready -> Home()
        }
    }
}

@Composable
fun MissingPermission(
    modifier: Modifier = Modifier,
    missingPermission: State.MissingPermission,
    trigger: () -> Unit,
) {
    val activity = LocalContext.current as Activity
    val hasRefusedGrant = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(RequestMultiplePermissions()) { granted ->
        if (granted.values.any { it.not() }) hasRefusedGrant.value = true
        else trigger()
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (hasRefusedGrant.value)
                "Failed to obtain permission, please update this in the app's system settings"
            else
                "TPMS Advanced needs some permission to continue.\nTheses are required by the system in order to make BLE scan",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 8.dp)
        )
        FilledTonalButton(onClick = {
            if (hasRefusedGrant.value)
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .apply { addCategory(CATEGORY_DEFAULT) }
                    .apply { data = "package:${activity.packageName}".toUri() }
                    .apply { addFlags(FLAG_ACTIVITY_NEW_TASK) }
                    .apply { addFlags(FLAG_ACTIVITY_NO_HISTORY) }
                    .apply { addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
                    .also { activity.startActivity(it) }
            else
                launcher.launch(missingPermission.permissions.toTypedArray())
        }) {
            Text(
                text = if (hasRefusedGrant.value) "Open settings"
                else "Grant permission"
            )
        }
    }
}

@Composable
fun ChipIsOff(
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as Activity
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "TPMS Advanced needs you to enable the bluetooth chip.\nThis is required by the system in order to make BLE scan",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 8.dp)
        )
        FilledTonalButton(onClick = {
            activity.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }) {
            Text("Enable bluetooth")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    LazyColumn {
        items(PreconditionsViewModel.mocks()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Main(viewModel = it)
            }
        }
    }
}
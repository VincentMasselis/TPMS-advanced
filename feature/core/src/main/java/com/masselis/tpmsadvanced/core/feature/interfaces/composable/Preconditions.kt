package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.ui.MissingPermission
import com.masselis.tpmsadvanced.core.ui.OnLifecycleEvent
import kotlinx.collections.immutable.toImmutableList

@Composable
public fun Preconditions(
    ready: @Composable () -> Unit
): Unit = InternalPreconditions(ready)

@Suppress("NAME_SHADOWING")
@Composable
internal fun InternalPreconditions(
    ready: @Composable () -> Unit,
    viewModel: PreconditionsViewModel = viewModel {
        FeatureCoreComponent.preconditionsViewModel.build(createSavedStateHandle())
    }
) {
    OnLifecycleEvent { _, event ->
        if (event == Lifecycle.Event.ON_RESUME)
            viewModel.trigger()
    }
    val state by viewModel.stateFlow.collectAsState()
    when (val state = state) {
        State.Loading -> {}
        State.BluetoothChipTurnedOff -> ChipIsOff(modifier = Modifier.fillMaxSize())
        is State.MissingPermission -> MissingPermission(
            @Suppress("MaxLineLength")
            "TPMS Advanced needs some permission to continue.\nTheses are required by the system in order to make BLE scan",
            "Failed to obtain permission, please update this in the app's system settings",
            missingPermissions = state.permissions.toImmutableList(),
            modifier = Modifier.fillMaxSize(),
        ) { viewModel.trigger() }
        State.Ready -> ready()
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun ChipIsOff(
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as Activity
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            @Suppress("MaxLineLength")
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

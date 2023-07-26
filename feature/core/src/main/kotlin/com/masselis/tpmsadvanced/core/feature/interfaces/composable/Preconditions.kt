package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.ui.BluetoothState
import com.masselis.tpmsadvanced.core.ui.MissingPermission
import com.masselis.tpmsadvanced.core.ui.rememberBluetoothState

@Composable
public fun Preconditions(
    modifier: Modifier = Modifier,
    ready: @Composable () -> Unit,
): Unit = InternalPreconditions(ready, modifier)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun InternalPreconditions(
    ready: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PreconditionsViewModel = viewModel {
        FeatureCoreComponent.preconditionsViewModel
    }
) {
    val permissionState = rememberMultiplePermissionsState(viewModel.requiredPermission())
    val bluetoothState = rememberBluetoothState()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "ready",
        modifier = modifier,
    ) {
        composable("missing_permission") {
            @Suppress("MaxLineLength")
            MissingPermission(
                text = "TPMS Advanced needs some permission to continue.\nTheses are required by the system in order to make BLE scan",
                refusedText = "Failed to obtain permission, please update this in the app's system settings",
                permissionState = permissionState,
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable("chip_is_off") {
            ChipIsOff(
                bluetoothState = bluetoothState,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable("ready") {
            ready()
        }
    }
    when {
        permissionState.allPermissionsGranted.not() ->
            navController.navigate("missing_permission") { popUpTo(0) }

        viewModel.isBluetoothRequired() && bluetoothState.isEnabled.not() ->
            navController.navigate("chip_is_off") { popUpTo(0) }

        else ->
            navController.navigate("ready") { popUpTo(0) }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun ChipIsOff(
    bluetoothState: BluetoothState,
    modifier: Modifier = Modifier
) {
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
            bluetoothState.askEnable()
        }) {
            Text("Enable bluetooth")
        }
    }
}

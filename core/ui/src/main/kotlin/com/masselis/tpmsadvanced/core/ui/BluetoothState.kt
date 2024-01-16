package com.masselis.tpmsadvanced.core.ui

import android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.STATE_OFF
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import com.masselis.tpmsadvanced.core.common.asFlow
import kotlinx.coroutines.flow.map


@Composable
public fun rememberBluetoothState(): BluetoothState {
    val context = LocalContext.current
    val state = remember(context) { MutableBluetoothState(context) }
    LaunchedEffect(state) {
        IntentFilter(ACTION_STATE_CHANGED)
            .asFlow()
            .map { it.getIntExtra(EXTRA_STATE, STATE_OFF) == STATE_ON }
            .collect { state.isEnabled = it }
    }
    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) {}
    DisposableEffect(state, launcher) {
        state.launcher = launcher
        onDispose {
            state.launcher = null
        }
    }
    return state
}

@Stable
private class MutableBluetoothState(context: Context) : BluetoothState {

    var launcher: ActivityResultLauncher<Intent>? = null

    override var isEnabled: Boolean by mutableStateOf(
        context.getSystemService<BluetoothManager>()?.adapter?.isEnabled ?: false
    )

    override fun askEnable() {
        launcher?.launch(Intent(ACTION_REQUEST_ENABLE)) ?: error("launcher cannot be null")
    }
}

@Stable
public interface BluetoothState {
    public val isEnabled: Boolean
    public fun askEnable()
}

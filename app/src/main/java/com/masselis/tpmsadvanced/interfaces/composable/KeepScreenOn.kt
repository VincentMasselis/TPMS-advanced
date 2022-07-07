package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.masselis.tpmsadvanced.interfaces.RootActivity

@Composable
fun KeepScreenOn() {
    val activity = LocalContext.current as RootActivity
    DisposableEffect(Unit) {
        activity.incrementScreenOnCounter()
        onDispose {
            activity.decrementScreenOnCounter()
        }
    }
}
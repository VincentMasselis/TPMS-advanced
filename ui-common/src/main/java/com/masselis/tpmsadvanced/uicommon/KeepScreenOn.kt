package com.masselis.tpmsadvanced.uicommon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf

val KeepScreenOnCounter = compositionLocalOf<ScreenOnCounter> {
    error("Not available")
}

@Composable
fun KeepScreenOn() {
    val counter = KeepScreenOnCounter.current
    DisposableEffect(Unit) {
        counter.increment()
        onDispose {
            counter.decrement()
        }
    }
}
package com.masselis.tpmsadvanced.core.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

@SuppressLint("ComposeCompositionLocalUsage")
public val LocalKeepScreenOnCounter: ProvidableCompositionLocal<ScreenOnCounter> =
    staticCompositionLocalOf { error("Not available") }

@Composable
public fun KeepScreenOn() {
    val counter = LocalKeepScreenOnCounter.current
    DisposableEffect(Unit) {
        counter.increment()
        onDispose {
            counter.decrement()
        }
    }
}

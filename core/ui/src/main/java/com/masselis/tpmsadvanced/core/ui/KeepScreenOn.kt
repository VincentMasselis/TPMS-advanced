package com.masselis.tpmsadvanced.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

public val LocalKeepScreenOnCounter: ProvidableCompositionLocal<ScreenOnCounter> =
    compositionLocalOf { error("Not available") }

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

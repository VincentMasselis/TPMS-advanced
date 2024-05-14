package com.masselis.tpmsadvanced.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

@Suppress("CompositionLocalAllowlist")
public val LocalKeepScreenOnCounter: ProvidableCompositionLocal<ScreenOnCounter> =
    staticCompositionLocalOf { error("Not available") }

@Composable
public fun KeepScreenOn() {
    if (LocalContext.current.findActivity()::class.qualifiedName == "androidx.compose.ui.tooling.PreviewActivity")
        return
    val counter = LocalKeepScreenOnCounter.current
    DisposableEffect(Unit) {
        counter.increment()
        onDispose {
            counter.decrement()
        }
    }
}

package com.masselis.tpmsadvanced.core.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

@SuppressLint("ComposeCompositionLocalUsage")
public val LocalHomeNavController: ProvidableCompositionLocal<NavController> =
    staticCompositionLocalOf { error("Nav controller not available") }

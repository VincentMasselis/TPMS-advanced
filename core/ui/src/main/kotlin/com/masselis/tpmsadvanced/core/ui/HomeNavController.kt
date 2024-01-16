package com.masselis.tpmsadvanced.core.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController


@Suppress("CompositionLocalAllowlist")
public val LocalHomeNavController: ProvidableCompositionLocal<NavController> =
    staticCompositionLocalOf { error("Nav controller not available") }

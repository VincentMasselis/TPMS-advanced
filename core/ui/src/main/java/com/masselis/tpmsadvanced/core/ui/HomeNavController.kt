package com.masselis.tpmsadvanced.core.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

public val LocalHomeNavController: ProvidableCompositionLocal<NavController> = compositionLocalOf {
    error("Nav controller not available")
}

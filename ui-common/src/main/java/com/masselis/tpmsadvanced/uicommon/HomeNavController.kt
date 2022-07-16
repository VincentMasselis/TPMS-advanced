package com.masselis.tpmsadvanced.uicommon

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val HomeNavController = compositionLocalOf<NavController> {
    error("Nav controller not available")
}
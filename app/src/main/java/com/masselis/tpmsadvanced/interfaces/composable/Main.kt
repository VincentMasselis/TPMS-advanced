package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.runtime.Composable
import com.masselis.tpmsadvanced.core.interfaces.composable.Preconditions

@Suppress("NAME_SHADOWING")
@Composable
fun Main() {
    Preconditions {
        Home()
    }
}
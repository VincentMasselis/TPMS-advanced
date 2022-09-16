package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.runtime.Composable
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.Preconditions

@Suppress("NAME_SHADOWING")
@Composable
internal fun Main() {
    Preconditions {
        Home()
    }
}

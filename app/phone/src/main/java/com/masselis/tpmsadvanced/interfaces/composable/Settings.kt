package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.coreSettings

@Composable
internal fun Settings(
    modifier: Modifier = Modifier
) = LazyColumn(
    modifier = modifier.padding(end = 16.dp, start = 16.dp)
) {
    coreSettings()
}

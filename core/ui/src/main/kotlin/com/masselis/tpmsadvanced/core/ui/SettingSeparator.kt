package com.masselis.tpmsadvanced.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun Separator(modifier: Modifier = Modifier) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(24.dp))
        Divider(thickness = Dp.Hairline)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

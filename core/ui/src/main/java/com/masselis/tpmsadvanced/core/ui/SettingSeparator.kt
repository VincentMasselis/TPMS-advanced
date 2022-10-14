package com.masselis.tpmsadvanced.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public fun LazyListScope.separator(): Unit = item {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Divider(thickness = Dp.Hairline)
        Spacer(modifier = Modifier.height(24.dp))
    }
}
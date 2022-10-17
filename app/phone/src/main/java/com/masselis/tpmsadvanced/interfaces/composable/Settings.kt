package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.BuildConfig
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.coreSettings
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.Units
import com.masselis.tpmsadvanced.core.ui.separator

@Composable
internal fun Settings(
    modifier: Modifier = Modifier
) = LazyColumn(
    modifier = modifier.padding(end = 16.dp, start = 16.dp)
) {
    item { Units() }
    separator()
    coreSettings()
    separator()
    item {
        Box(Modifier.fillMaxWidth()) {
            Text(
                "Version code: ${BuildConfig.VERSION_CODE}",
                Modifier.align(Alignment.TopEnd),
                color = Color.Gray
            )
        }
    }
    item { Spacer(modifier = Modifier.height(56.dp)) }
}

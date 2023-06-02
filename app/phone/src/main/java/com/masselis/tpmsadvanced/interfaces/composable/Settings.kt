package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.BuildConfig
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.VehicleSettings
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.UnitsSettings
import com.masselis.tpmsadvanced.core.ui.Separator

@Composable
internal fun Settings(
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .padding(end = 16.dp, start = 16.dp)
        .verticalScroll(rememberScrollState())
) {
    UnitsSettings()
    Separator()
    VehicleSettings()
    Separator()
    Box(Modifier.fillMaxWidth()) {
        Text(
            "Version code: ${BuildConfig.VERSION_CODE}",
            Modifier.align(Alignment.TopEnd),
            color = Color.Gray
        )
    }

    Spacer(modifier = Modifier.height(56.dp))
}

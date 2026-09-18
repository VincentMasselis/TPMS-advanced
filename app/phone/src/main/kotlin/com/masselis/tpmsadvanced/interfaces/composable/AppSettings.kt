package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.TyreDisplaySettings
import com.masselis.tpmsadvanced.interfaces.composable.AppSettingsTag.tyreDisplay

@Composable
internal fun AppSettings(
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .padding(end = 16.dp, start = 16.dp)
        .verticalScroll(rememberScrollState())
) {
    SettingsSectionHeader("Display settings")
    TyreDisplaySettings(Modifier.testTag(tyreDisplay))
}

@Composable
private fun SettingsSectionHeader(
    text: String,
    modifier: Modifier = Modifier
) = Text(
    text = text,
    style = MaterialTheme.typography.titleSmall,
    color = MaterialTheme.colorScheme.primary,
    modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
)

internal object AppSettingsTag {
    const val tyreDisplay = "AppSettingsTag_tyreDisplay"
}

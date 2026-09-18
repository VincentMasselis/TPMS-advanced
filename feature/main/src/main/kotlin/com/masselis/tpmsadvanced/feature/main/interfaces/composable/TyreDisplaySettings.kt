package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreDisplaySettingsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.Bindings.Companion.TyreDisplaySettingsViewModel

@Composable
public fun TyreDisplaySettings(modifier: Modifier = Modifier): Unit =
    TyreDisplaySettings(
        modifier,
        viewModel { TyreDisplaySettingsViewModel() },
    )

@Composable
internal fun TyreDisplaySettings(
    modifier: Modifier = Modifier,
    viewModel: TyreDisplaySettingsViewModel = viewModel { TyreDisplaySettingsViewModel() },
) {
    val showTimestamp by viewModel.showTimestamp.collectAsState()
    val showSensorId by viewModel.showSensorId.collectAsState()
    val showTimeSinceUpdate by viewModel.showTimeSinceUpdate.collectAsState()
    TyreDisplaySettings(
        showTimestamp = showTimestamp,
        onShowTimestamp = { viewModel.showTimestamp.value = it },
        showSensorId = showSensorId,
        onShowSensorId = { viewModel.showSensorId.value = it },
        showTimeSinceUpdate = showTimeSinceUpdate,
        onShowTimeSinceUpdate = { viewModel.showTimeSinceUpdate.value = it },
        modifier = modifier,
    )
}

@Composable
private fun TyreDisplaySettings(
    showTimestamp: Boolean,
    onShowTimestamp: (Boolean) -> Unit,
    showSensorId: Boolean,
    onShowSensorId: (Boolean) -> Unit,
    showTimeSinceUpdate: Boolean,
    onShowTimeSinceUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    CheckboxRow(
        text = "Show time since update",
        checked = showTimeSinceUpdate,
        onCheckedChange = onShowTimeSinceUpdate,
        modifier = Modifier.testTag(TyreDisplaySettingsTags.showTimeSinceUpdate),
    )
    CheckboxRow(
        text = "Show timestamp",
        checked = showTimestamp,
        onCheckedChange = onShowTimestamp,
        modifier = Modifier.testTag(TyreDisplaySettingsTags.showTimestamp),
    )
    CheckboxRow(
        text = "Show ID",
        checked = showSensorId,
        onCheckedChange = onShowSensorId,
        modifier = Modifier.testTag(TyreDisplaySettingsTags.showSensorId),
    )
}

@Composable
private fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier,
) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    Text(text)
}

@Preview
@Composable
internal fun TyreDisplaySettingsPreview() {
    TyreDisplaySettings(
        showTimestamp = true,
        onShowTimestamp = {},
        showSensorId = true,
        onShowSensorId = {},
        showTimeSinceUpdate = true,
        onShowTimeSinceUpdate = {},
    )
}

@Suppress("ConstPropertyName")
internal object TyreDisplaySettingsTags {
    const val showTimestamp = "TyreDisplaySettingsTags_showTimestamp"
    const val showSensorId = "TyreDisplaySettingsTags_showSensorId"
    const val showTimeSinceUpdate = "TyreDisplaySettingsTags_showTimeSinceUpdate"
}

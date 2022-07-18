package com.masselis.tpmsadvanced.core.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.interfaces.coreComponent
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.core.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.core.model.Fraction
import com.masselis.tpmsadvanced.core.model.Temperature.CREATOR.celsius

fun LazyListScope.coreSettings() {
    item { Units() }
    separator()
    item { LowPressure() }
    separator()
    item { HighTemp() }
    item { NormalTemp() }
    item { LowTemp() }
    separator()
    item { ClearFavourites(Modifier.fillMaxWidth()) }
}

@Composable
private fun LowPressure(
    viewModel: SettingsViewModel = viewModel {
        coreComponent.settingsViewModel.build(createSavedStateHandle())
    }
) {
    var showLowPressureDialog by remember { mutableStateOf(false) }
    LowPressureSlider(
        { showLowPressureDialog = true },
        viewModel.lowPressure,
        viewModel.pressureUnit
    )
    if (showLowPressureDialog)
        LowPressureInfo(viewModel.lowPressure, viewModel.pressureUnit) {
            showLowPressureDialog = false
        }
}

@Composable
private fun HighTemp(
    viewModel: SettingsViewModel = viewModel {
        coreComponent.settingsViewModel.build(createSavedStateHandle())
    }
) {
    var showHighTempDialog by remember { mutableStateOf(false) }
    val normalTemp by viewModel.normalTemp.collectAsState()
    TemperatureSlider(
        { showHighTempDialog = true },
        "Max temperature:",
        mutableStateFlow = viewModel.highTemp,
        unitFlow = viewModel.temperatureUnit,
        valueRange = normalTemp..(150f.celsius),
    )
    if (showHighTempDialog)
        TemperatureInfo(
            text = "When the temperature is equals or superior to %s, the tyre starts to blink in red to alert you",
            state = TyreViewModel.State.Alerting,
            temperatureStateFlow = viewModel.highTemp,
            temperatureUnit = viewModel.temperatureUnit,
        ) { showHighTempDialog = false }
}

@Composable
private fun NormalTemp(
    viewModel: SettingsViewModel = viewModel {
        coreComponent.settingsViewModel.build(createSavedStateHandle())
    }
) {
    var showNormalTempDialog by remember { mutableStateOf(false) }
    val lowTemp by viewModel.lowTemp.collectAsState()
    val highTemp by viewModel.highTemp.collectAsState()
    TemperatureSlider(
        { showNormalTempDialog = true },
        "Normal temperature:",
        mutableStateFlow = viewModel.normalTemp,
        unitFlow = viewModel.temperatureUnit,
        valueRange = lowTemp..highTemp
    )
    if (showNormalTempDialog)
        TemperatureInfo(
            text = "When the temperature is close to %s, the tyre is colored in green",
            state = TyreViewModel.State.Normal.BlueToGreen(Fraction(1f)),
            temperatureStateFlow = viewModel.normalTemp,
            temperatureUnit = viewModel.temperatureUnit,
        ) { showNormalTempDialog = false }
}

@Composable
private fun LowTemp(
    viewModel: SettingsViewModel = viewModel {
        coreComponent.settingsViewModel.build(createSavedStateHandle())
    }
) {
    var showLowTempDialog by remember { mutableStateOf(false) }
    val normalTemp by viewModel.normalTemp.collectAsState()
    TemperatureSlider(
        { showLowTempDialog = true },
        "Low temperature:",
        mutableStateFlow = viewModel.lowTemp,
        unitFlow = viewModel.temperatureUnit,
        valueRange = 5f.celsius..normalTemp
    )
    if (showLowTempDialog)
        TemperatureInfo(
            text = "When the temperature is close to %s, the tyre is colored in blue",
            state = TyreViewModel.State.Normal.BlueToGreen(Fraction(0f)),
            temperatureStateFlow = viewModel.lowTemp,
            temperatureUnit = viewModel.temperatureUnit,
        ) { showLowTempDialog = false }
}

private fun LazyListScope.separator() = item {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Divider(thickness = Dp.Hairline)
        Spacer(modifier = Modifier.height(24.dp))
    }
}
package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.interfaces.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.model.Fraction

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel {
        mainComponent.settingsViewModel.build(createSavedStateHandle())
    }
) {
    val highTempFlow = viewModel.highTemp
    val normalTempFlow = viewModel.normalTemp
    val lowTempFlow = viewModel.lowTemp
    var showLowPressureDialog by remember { mutableStateOf(false) }
    var showLowTempDialog by remember { mutableStateOf(false) }
    var showNormalTempDialog by remember { mutableStateOf(false) }
    var showHighTempDialog by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier.padding(end = 16.dp, start = 16.dp)
    ) {
        item {
            LowPressureSlider(
                { showLowPressureDialog = true },
                viewModel.lowPressure
            )
        }
        item {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                Divider(thickness = Dp.Hairline)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        item {
            Column {
                val normalTemp by normalTempFlow.collectAsState()
                TemperatureSlider(
                    { showHighTempDialog = true },
                    "Max temperature:",
                    mutableStateFlow = highTempFlow,
                    valueRange = normalTemp.celsius..150f
                )
            }
        }
        item {
            Column {
                val lowTemp by lowTempFlow.collectAsState()
                val highTemp by highTempFlow.collectAsState()
                TemperatureSlider(
                    { showNormalTempDialog = true },
                    "Normal temperature:",
                    mutableStateFlow = normalTempFlow,
                    valueRange = lowTemp.celsius..highTemp.celsius
                )
            }
        }
        item {
            val normalTemp by normalTempFlow.collectAsState()
            TemperatureSlider(
                { showLowTempDialog = true },
                "Low temperature:",
                mutableStateFlow = lowTempFlow,
                valueRange = 5f..normalTemp.celsius
            )
        }
        item {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                Divider(thickness = Dp.Hairline)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        item { Units() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { ClearFavourites(Modifier.fillMaxWidth()) }
    }
    if (showLowPressureDialog)
        LowPressureInfo(viewModel.lowPressure) { showLowPressureDialog = false }
    if (showLowTempDialog)
        TemperatureInfo(
            text = "When the temperature is close to %.1f°C, the tyre is colored in blue",
            state = TyreViewModel.State.Normal.BlueToGreen(Fraction(0f)),
            temperatureStateFlow = lowTempFlow,
        ) { showLowTempDialog = false }
    if (showNormalTempDialog)
        TemperatureInfo(
            text = "When the temperature is close to %.1f°C, the tyre is colored in green",
            state = TyreViewModel.State.Normal.BlueToGreen(Fraction(1f)),
            temperatureStateFlow = normalTempFlow
        ) { showNormalTempDialog = false }
    if (showHighTempDialog)
        TemperatureInfo(
            text = "When the temperature is equals or superior to %.1f°C, the tyre starts to blink in red to alert you",
            state = TyreViewModel.State.Alerting,
            temperatureStateFlow = highTempFlow
        ) { showHighTempDialog = false }
}
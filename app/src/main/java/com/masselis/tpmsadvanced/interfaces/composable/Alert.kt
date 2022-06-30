package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.interfaces.mainComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.AlertViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.utils.savedStateViewModel
import com.masselis.tpmsadvanced.mock.mocks
import com.masselis.tpmsadvanced.model.Fraction

@Composable
fun Alert(
    modifier: Modifier = Modifier,
    viewModel: AlertViewModel = savedStateViewModel { mainComponent.alertViewModel.build(it) }
) {
    val highTempFlow = viewModel.highTemp
    val normalTempFlow = viewModel.normalTemp
    val lowTempFlow = viewModel.lowTemp
    var showLowPressureDialog by remember { mutableStateOf(false) }
    var showLowTempDialog by remember { mutableStateOf(false) }
    var showNormalTempDialog by remember { mutableStateOf(false) }
    var showHighTempDialog by remember { mutableStateOf(false) }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = modifier.padding(end = 16.dp, start = 16.dp)
    ) {
        item {
            LowPressureSlider(
                { showLowPressureDialog = true },
                viewModel.lowPressure
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Divider(thickness = Dp.Hairline)
            Spacer(modifier = Modifier.height(24.dp))
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

@Preview
@Composable
fun AlertPreview() {
    TpmsAdvancedTheme {
        AlertViewModel.mocks().forEach { mock ->
            Alert(
                viewModel = mock,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
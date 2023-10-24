package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.ui.Separator
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius

@Composable
public fun VehicleSettings(
    modifier: Modifier = Modifier,
    backgroundSettings: @Composable (VehicleComponent) -> Unit = backgroundSettingsPlaceholder,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
) {
    VehicleSettings(
        modifier,
        backgroundSettings,
        vehicleComponent,
        viewModel(key = "VehicleSettingsViewModel_${vehicleComponent.vehicle.uuid}") {
            vehicleComponent.vehicleSettingsViewModel.build()
        }
    )
}

@Composable
internal fun VehicleSettings(
    modifier: Modifier = Modifier,
    backgroundSettings: @Composable (VehicleComponent) -> Unit = backgroundSettingsPlaceholder,
    vehicleComponent: VehicleComponent = LocalVehicleComponent.current,
    vehicleSettingsViewModel: VehicleSettingsViewModel = viewModel(
        key = "VehicleSettingsViewModel_${vehicleComponent.vehicle.uuid}"
    ) {
        vehicleComponent.vehicleSettingsViewModel.build()
    }
) {
    val component = LocalVehicleComponent.current
    Column(modifier) {
        PressureRange(vehicleSettingsViewModel)
        Separator()
        HighTemp(vehicleSettingsViewModel)
        NormalTemp(vehicleSettingsViewModel)
        LowTemp(vehicleSettingsViewModel)
        if (backgroundSettings !== backgroundSettingsPlaceholder) {
            Separator()
            backgroundSettings(component)
        }
        Separator()
        ClearBoundSensorsButton(Modifier.fillMaxWidth())
        Separator()
        DeleteVehicleButton(Modifier.fillMaxWidth())
    }
}

@Composable
private fun PressureRange(viewModel: VehicleSettingsViewModel) {
    var showLowPressureDialog by remember { mutableStateOf(false) }
    val lowPressure by viewModel.lowPressure.collectAsState()
    val highPressure by viewModel.highPressure.collectAsState()
    val unit by viewModel.pressureUnit.collectAsState()
    PressureRangeSlider(
        { showLowPressureDialog = true },
        lowPressure..highPressure,
        unit,
        0.5f.bar..5f.bar,
    ) { range ->
        viewModel.lowPressure.value = range.start
        viewModel.highPressure.value = range.endInclusive
    }
    if (showLowPressureDialog)
        PressureInfo(lowPressure..highPressure, unit) {
            showLowPressureDialog = false
        }
}

@Composable
private fun HighTemp(viewModel: VehicleSettingsViewModel) {
    var showHighTempDialog by remember { mutableStateOf(false) }
    val highTemp by viewModel.highTemp.collectAsState()
    val normalTemp by viewModel.normalTemp.collectAsState()
    val unit by viewModel.temperatureUnit.collectAsState()
    TemperatureSlider(
        { showHighTempDialog = true },
        "Max temperature:",
        highTemp,
        unit,
        { viewModel.highTemp.value = it },
        normalTemp..(150f.celsius),
    )
    if (showHighTempDialog)
        TemperatureInfo(
            text = "When the temperature is equals or superior to %s, the tyre starts to blink in red to alert you",
            state = State.Alerting,
            temperature = highTemp,
            unit = unit,
        ) { showHighTempDialog = false }
}

@Composable
private fun NormalTemp(viewModel: VehicleSettingsViewModel) {
    var showNormalTempDialog by remember { mutableStateOf(false) }
    val lowTemp by viewModel.lowTemp.collectAsState()
    val normalTemp by viewModel.normalTemp.collectAsState()
    val highTemp by viewModel.highTemp.collectAsState()
    val unit by viewModel.temperatureUnit.collectAsState()
    TemperatureSlider(
        { showNormalTempDialog = true },
        "Normal temperature:",
        normalTemp,
        unit,
        { viewModel.normalTemp.value = it },
        lowTemp..highTemp
    )
    if (showNormalTempDialog)
        TemperatureInfo(
            text = "When the temperature is close to %s, the tyre is colored in green",
            state = State.Normal.BlueToGreen(Fraction(1f)),
            temperature = normalTemp,
            unit = unit,
        ) { showNormalTempDialog = false }
}

@Composable
private fun LowTemp(viewModel: VehicleSettingsViewModel) {
    var showLowTempDialog by remember { mutableStateOf(false) }
    val lowTemp by viewModel.lowTemp.collectAsState()
    val normalTemp by viewModel.normalTemp.collectAsState()
    val unit by viewModel.temperatureUnit.collectAsState()
    TemperatureSlider(
        { showLowTempDialog = true },
        "Low temperature:",
        lowTemp,
        unit,
        { viewModel.lowTemp.value = it },
        5f.celsius..normalTemp
    )
    if (showLowTempDialog) TemperatureInfo(
        text = "When the temperature is close to %s, the tyre is colored in blue",
        state = State.Normal.BlueToGreen(Fraction(0f)),
        temperature = lowTemp,
        unit = unit,
    ) { showLowTempDialog = false }
}

private val backgroundSettingsPlaceholder: @Composable (VehicleComponent) -> Unit = {}

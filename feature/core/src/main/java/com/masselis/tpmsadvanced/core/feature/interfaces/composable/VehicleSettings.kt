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
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleComponentViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.ui.Separator
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius

@Composable
public fun VehicleSettings(
    backgroundSettings: @Composable (VehicleComponent) -> Unit,
    modifier: Modifier = Modifier
): Unit = VehicleSettings(
    backgroundSettings,
    modifier,
    viewModel { FeatureCoreComponent.currentVehicleComponentViewModel.build(createSavedStateHandle()) }
)

@Composable
internal fun VehicleSettings(
    backgroundSettings: @Composable (VehicleComponent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CurrentVehicleComponentViewModel = viewModel {
        FeatureCoreComponent.currentVehicleComponentViewModel.build(createSavedStateHandle())
    }
) {
    val component by viewModel.stateFlow.collectAsState()
    Column(modifier) {
        PressureRange(component)
        Separator()
        HighTemp(component)
        NormalTemp(component)
        LowTemp(component)
        Separator()
        backgroundSettings(component)
        Separator()
        ClearBoundSensorsButton(component, Modifier.fillMaxWidth())
        Separator()
        DeleteVehicleButton(component, Modifier.fillMaxWidth())
    }
}

@Composable
private fun PressureRange(
    vehicleComponent: VehicleComponent,
    viewModel: VehicleSettingsViewModel = viewModel(key = "SettingsViewModel_${vehicleComponent.hashCode()}") {
        vehicleComponent.vehicleSettingsViewModel.build()
    }
) {
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
private fun HighTemp(
    vehicleComponent: VehicleComponent,
    viewModel: VehicleSettingsViewModel = viewModel(key = "SettingsViewModel_${vehicleComponent.hashCode()}") {
        vehicleComponent.vehicleSettingsViewModel.build()
    }
) {
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
private fun NormalTemp(
    vehicleComponent: VehicleComponent,
    viewModel: VehicleSettingsViewModel = viewModel(key = "SettingsViewModel_${vehicleComponent.hashCode()}") {
        vehicleComponent.vehicleSettingsViewModel.build()
    }
) {
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
private fun LowTemp(
    vehicleComponent: VehicleComponent,
    viewModel: VehicleSettingsViewModel = viewModel(key = "SettingsViewModel_${vehicleComponent.hashCode()}") {
        vehicleComponent.vehicleSettingsViewModel.build()
    }
) {
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

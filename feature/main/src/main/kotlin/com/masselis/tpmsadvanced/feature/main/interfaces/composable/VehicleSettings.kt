package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.ui.Separator
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.InternalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent.Companion.key
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State

@Composable
public fun VehicleSettings(
    modifier: Modifier = Modifier,
    backgroundSettings: @Composable (VehicleComponent) -> Unit = backgroundSettingsPlaceholder,
    component: VehicleComponent = LocalVehicleComponent.current,
) {
    VehicleSettings(
        modifier,
        backgroundSettings,
        component as InternalVehicleComponent,
    )
}

@Composable
internal fun VehicleSettings(
    modifier: Modifier = Modifier,
    backgroundSettings: @Composable (VehicleComponent) -> Unit = backgroundSettingsPlaceholder,
    component: InternalVehicleComponent = LocalInternalVehicleComponent.current,
    viewModel: VehicleSettingsViewModel = component.viewModel(component.key()) { it.VehicleSettingsViewModel() },
) {
    val component = LocalVehicleComponent.current
    val highTemp by viewModel.highTemp.collectAsState()
    val normalTemp by viewModel.normalTemp.collectAsState()
    val lowTemp by viewModel.lowTemp.collectAsState()
    val tempUnit by viewModel.temperatureUnit.collectAsState()
    Column(modifier) {
        with(viewModel) {
            PressureRange(
                lowPressure.collectAsState().value,
                highPressure.collectAsState().value,
                pressureUnit.collectAsState().value,
                { lowPressure.value = it },
                { highPressure.value = it },
            )
        }
        Separator()
        HighTemp(highTemp, normalTemp, tempUnit, { viewModel.highTemp.value = it })
        NormalTemp(lowTemp, normalTemp, highTemp, tempUnit, { viewModel.normalTemp.value = it })
        LowTemp(lowTemp, normalTemp, tempUnit, { viewModel.lowTemp.value = it })
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
private fun PressureRange(
    lowPressure: Pressure,
    highPressure: Pressure,
    unit: PressureUnit,
    onLowPressure: (Pressure) -> Unit,
    onHighPressure: (Pressure) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLowPressureDialog by remember { mutableStateOf(false) }
    PressureRangeSlider(
        minMaxRange = 0.5f.bar..5f.bar,
        values = lowPressure..highPressure,
        onValue = {
            onLowPressure(it.start)
            onHighPressure(it.endInclusive)
        },
        openInfo = { showLowPressureDialog = true },
        unit = unit,
        modifier = modifier
    )
    if (showLowPressureDialog)
        PressureInfo(
            pressureRange = lowPressure..highPressure,
            unit = unit,
            onDismissRequest = { showLowPressureDialog = false }
        )
}

@Composable
private fun HighTemp(
    highTemp: Temperature,
    normalTemp: Temperature,
    unit: TemperatureUnit,
    onHighTemp: (Temperature) -> Unit,
    modifier: Modifier = Modifier
) {
    var showHighTempDialog by remember { mutableStateOf(false) }
    TemperatureSlider(
        openInfo = { showHighTempDialog = true },
        title = "Max temperature:",
        value = highTemp,
        unit = unit,
        onValue = onHighTemp,
        minMaxRange = normalTemp..(150f.celsius),
        modifier = modifier
    )
    if (showHighTempDialog) TemperatureInfo(
        text = "When the temperature is equals or superior to %s, the tyre starts to blink in red to alert you",
        state = State.Alerting,
        temperature = highTemp,
        unit = unit,
    ) { showHighTempDialog = false }
}

@Composable
private fun NormalTemp(
    lowTemp: Temperature,
    normalTemp: Temperature,
    highTemp: Temperature,
    unit: TemperatureUnit,
    onNormalTemp: (Temperature) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNormalTempDialog by remember { mutableStateOf(false) }
    TemperatureSlider(
        openInfo = { showNormalTempDialog = true },
        title = "Normal temperature:",
        value = normalTemp,
        unit = unit,
        onValue = onNormalTemp,
        minMaxRange = lowTemp..highTemp,
        modifier = modifier
    )
    if (showNormalTempDialog) TemperatureInfo(
        text = "When the temperature is close to %s, the tyre is colored in green",
        state = State.Normal.BlueToGreen(Fraction(1f)),
        temperature = normalTemp,
        unit = unit,
    ) { showNormalTempDialog = false }
}

@Composable
private fun LowTemp(
    lowTemp: Temperature,
    normalTemp: Temperature,
    unit: TemperatureUnit,
    onLowTemp: (Temperature) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLowTempDialog by remember { mutableStateOf(false) }
    TemperatureSlider(
        openInfo = { showLowTempDialog = true },
        title = "Low temperature:",
        value = lowTemp,
        unit = unit,
        onValue = onLowTemp,
        minMaxRange = 5f.celsius..normalTemp,
        modifier = modifier
    )
    if (showLowTempDialog) TemperatureInfo(
        text = "When the temperature is close to %s, the tyre is colored in blue",
        state = State.Normal.BlueToGreen(Fraction(0f)),
        temperature = lowTemp,
        unit = unit,
    ) { showLowTempDialog = false }
}

private val backgroundSettingsPlaceholder: @Composable (VehicleComponent) -> Unit = {}

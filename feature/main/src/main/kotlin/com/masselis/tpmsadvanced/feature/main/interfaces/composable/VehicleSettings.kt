package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.ui.Separator
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.PressureCalibration
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleBindings.Companion.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent.Factory.Companion.key
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
        component,
        component.viewModel(component.key()) { it.VehicleSettingsViewModel() },
    )
}

@Suppress("LongMethod")
@Composable
internal fun VehicleSettings(
    modifier: Modifier = Modifier,
    backgroundSettings: @Composable (VehicleComponent) -> Unit = backgroundSettingsPlaceholder,
    component: VehicleComponent = LocalVehicleComponent.current,
    viewModel: VehicleSettingsViewModel = component.viewModel(component.key()) { it.VehicleSettingsViewModel() },
) {
    val highTemp by viewModel.highTemp.collectAsState()
    val normalTemp by viewModel.normalTemp.collectAsState()
    val lowTemp by viewModel.lowTemp.collectAsState()
    val tempUnit by viewModel.temperatureUnit.collectAsState()
    val hasFrontRearAxles = component.vehicle.kind.hasFrontRearAxles
    Column(modifier) {
        with(viewModel) {
            val pressureUnit by pressureUnit.collectAsState()
            val separateRear = hasFrontRearAxles && separateRearPressure.collectAsState().value
            if (hasFrontRearAxles)
                RearPressureToggle(
                    checked = separateRear,
                    onCheckedChange = { setRearOverrideEnabled(it) },
                )
            PressureRange(
                lowPressure.collectAsState().value,
                highPressure.collectAsState().value,
                pressureUnit,
                { lowPressure.value = it },
                { highPressure.value = it },
                title = if (separateRear)
                    "Front pressure range: "
                else
                    "Expected pressure range: ",
            )
            if (separateRear) {
                val rearLowPressureValue = rearLowPressure.collectAsState().value
                val rearHighPressureValue = rearHighPressure.collectAsState().value
                if (rearLowPressureValue != null && rearHighPressureValue != null)
                    PressureRange(
                        rearLowPressureValue,
                        rearHighPressureValue,
                        pressureUnit,
                        { rearLowPressure.value = it },
                        { rearHighPressure.value = it },
                        title = "Rear pressure range: ",
                    )
            }
            val low by lowPressure.collectAsState()
            val high by highPressure.collectAsState()
            PressureCalibrationSettings(
                enabled = pressureCalibration.collectAsState().value,
                calibration = PressureCalibration(
                    pressureOffset.collectAsState().value,
                    pressureMultiplier.collectAsState().value,
                ),
                // Somewhere the user expects their tyres to be, so the example speaks to them
                example = ((low.kpa + high.kpa) / 2).kpa,
                unit = pressureUnit,
                onEnabled = { pressureCalibration.value = it },
                onOffset = { pressureOffset.value = it },
                onMultiplier = { pressureMultiplier.value = it },
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
        Separator()
        DemoModeSwitch(Modifier.fillMaxWidth())
    }
}

@Composable
private fun PressureRange(
    lowPressure: Pressure,
    highPressure: Pressure,
    unit: PressureUnit,
    onLowPressure: (Pressure) -> Unit,
    onHighPressure: (Pressure) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Expected pressure range: ",
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
        modifier = modifier,
        title = title,
    )
    if (showLowPressureDialog)
        PressureInfo(
            pressureRange = lowPressure..highPressure,
            unit = unit,
            onDismissRequest = { showLowPressureDialog = false }
        )
}

@Composable
private fun RearPressureToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Set rear pressure separately",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
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

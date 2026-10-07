package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.ui.Separator
import com.masselis.tpmsadvanced.core.ui.viewModel
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleBindings.Companion.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent.Factory.Companion.key
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase.RearPressures
import kotlinx.coroutines.flow.MutableStateFlow

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
    Column(modifier) {
        with(viewModel) {
            PressureRange(
                unit = pressureUnit.collectAsState().value,
                lowPressure = lowPressure.collectAsState().value,
                onLowPressure = { lowPressure.value = it },
                highPressure = highPressure.collectAsState().value,
                onHighPressure = { highPressure.value = it },
                rearState = rearPressures.collectAsState().value,
                onSeparatePressureEnabled = { separated -> rearPressure(separated) },
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
    unit: PressureUnit,
    lowPressure: Pressure,
    onLowPressure: (Pressure) -> Unit,
    highPressure: Pressure,
    onHighPressure: (Pressure) -> Unit,
    onSeparatePressureEnabled: (Boolean) -> Unit,
    rearState: RearPressures,
    modifier: Modifier = Modifier
) {
    var showLowPressureDialog by remember { mutableStateOf(false) }
    Column(modifier) {
        PressureRangeSlider(
            axle = when (rearState) {
                is RearPressures.Enabled -> Location.Axle.FRONT
                RearPressures.Unavailable, is RearPressures.Disabled -> null
            },
            minMaxRange = 0.5f.bar..7f.bar,
            values = lowPressure..highPressure,
            onValue = {
                onLowPressure(it.start)
                onHighPressure(it.endInclusive)
            },
            openInfo = { showLowPressureDialog = true },
            unit = unit,
        )
        if (rearState is RearPressures.Available) {
            RearPressureRange(
                unit = unit,
                state = rearState,
                onSeparatePressureEnabled = onSeparatePressureEnabled
            )
        }
    }
    if (showLowPressureDialog)
        PressureInfo(
            pressureRange = lowPressure..highPressure,
            unit = unit,
            onDismissRequest = { showLowPressureDialog = false }
        )
}

@Composable
private fun ColumnScope.RearPressureRange(
    unit: PressureUnit,
    state: RearPressures.Available,
    onSeparatePressureEnabled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLowPressureDialog by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.align(Alignment.End)
    ) {
        Text("Use a different front/rear pressure")
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = when (state) {
                is RearPressures.Enabled -> true
                is RearPressures.Disabled -> false
            },
            onCheckedChange = onSeparatePressureEnabled,
            modifier = Modifier.testTag(VehicleSettingsTag.separateFrontRearPressureSwitch)
        )
    }
    AnimatedVisibility(
        when (state) {
            is RearPressures.Enabled -> true
            is RearPressures.Disabled -> false
        }
    ) {
        PressureRangeSlider(
            axle = Location.Axle.REAR,
            minMaxRange = 0.5f.bar..7f.bar,
            values = state.lowPressure.collectAsState().value..state.highPressure.collectAsState().value,
            onValue = {
                state.lowPressure.value = it.start
                state.highPressure.value = it.endInclusive
            },
            openInfo = { showLowPressureDialog = true },
            unit = unit,
            modifier = Modifier.testTag(VehicleSettingsTag.rearPressureRangeSlider)
        )
    }

    if (showLowPressureDialog)
        PressureInfo(
            pressureRange = state.lowPressure.collectAsState().value..state.highPressure.collectAsState().value,
            unit = unit,
            onDismissRequest = { showLowPressureDialog = false }
        )
}

@Preview(showBackground = true)
@Composable
private fun PressureRangePreview() {
    PressureRange(
        lowPressure = 1f.bar,
        highPressure = 3f.bar,
        rearState = RearPressures.Enabled(MutableStateFlow(1f.bar), MutableStateFlow(3F.bar)),
        onSeparatePressureEnabled = {},
        unit = PressureUnit.BAR,
        onLowPressure = {},
        onHighPressure = {},
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

internal object VehicleSettingsTag {
    const val separateFrontRearPressureSwitch = "VehicleSettingsTag_separateFrontRearPressureSwitch"
    const val rearPressureRangeSlider = "VehicleSettingsTag_rearPressureRangeSlider"
}

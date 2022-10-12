package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.feature.interfaces.featureCoreComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.FavouriteCarComponentViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.Units
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius

public fun LazyListScope.coreSettings() {
    item { Units() }
    separator()
    carItem { PressureRange() }
    separator()
    carItem { HighTemp() }
    carItem { NormalTemp() }
    carItem { LowTemp() }
    separator()
    carItem { ClearFavourites(Modifier.fillMaxWidth()) }
}

@Suppress("NAME_SHADOWING")
private fun LazyListScope.carItem(
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.() -> Unit
) = item(key, contentType) {
    val viewModel = viewModel { featureCoreComponent.favouriteCarComponentViewModel }
    val state by viewModel.stateFlow.collectAsState()
    val component = when (val state = state) {
        FavouriteCarComponentViewModel.State.Loading -> return@item
        is FavouriteCarComponentViewModel.State.Current -> state.component
    }
    CompositionLocalProvider(LocalCarComponent provides component) {
        content()
    }
}

@Composable
private fun PressureRange(
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: SettingsViewModel = viewModel(key = "SettingsViewModel_${carComponent.carId}") { carComponent.settingsViewModel.build() }
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
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: SettingsViewModel = viewModel(key = "SettingsViewModel_${carComponent.carId}") {
        carComponent.settingsViewModel.build()
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
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: SettingsViewModel = viewModel(key = "SettingsViewModel_${carComponent.carId}") { carComponent.settingsViewModel.build() }
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
    carComponent: CarComponent = LocalCarComponent.current,
    viewModel: SettingsViewModel = viewModel(key = "SettingsViewModel_${carComponent.carId}") { carComponent.settingsViewModel.build() }
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

private fun LazyListScope.separator() = item {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Divider(thickness = Dp.Hairline)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

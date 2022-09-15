package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.interfaces.AtmosphereRangePreferences
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
internal class SettingsViewModel @AssistedInject constructor(
    private val atmosphereRangePreferences: AtmosphereRangePreferences,
    unitPreferences: UnitPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): SettingsViewModel
    }

    val lowPressure = savedStateHandle
        .getLiveData("LOW_PRESSURE", atmosphereRangePreferences.lowPressureFlow.value)
        .asMutableStateFlow()

    val pressureUnit = unitPreferences.pressure.asStateFlow()

    val highTemp = savedStateHandle
        .getLiveData("HIGH_TEMP", atmosphereRangePreferences.highTempFlow.value)
        .asMutableStateFlow()

    val normalTemp = savedStateHandle
        .getLiveData("NORMAL_TEMP", atmosphereRangePreferences.normalTempFlow.value)
        .asMutableStateFlow()

    val lowTemp = savedStateHandle
        .getLiveData("LOW_TEMP", atmosphereRangePreferences.lowTempFlow.value)
        .asMutableStateFlow()

    val temperatureUnit = unitPreferences.temperature.asStateFlow()

    init {
        lowPressure
            .debounce(100.milliseconds)
            .onEach { atmosphereRangePreferences.lowPressureFlow.value = it }
            .launchIn(viewModelScope)

        highTemp
            .debounce(100.milliseconds)
            .onEach { atmosphereRangePreferences.highTempFlow.value = it }
            .launchIn(viewModelScope)

        normalTemp
            .debounce(100.milliseconds)
            .onEach { atmosphereRangePreferences.normalTempFlow.value = it }
            .launchIn(viewModelScope)

        lowTemp
            .debounce(100.milliseconds)
            .onEach { atmosphereRangePreferences.lowTempFlow.value = it }
            .launchIn(viewModelScope)
    }
}

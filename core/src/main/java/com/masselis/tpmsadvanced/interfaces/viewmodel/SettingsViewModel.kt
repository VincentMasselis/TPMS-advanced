package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.AtmosphereRangeUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SettingsViewModel @AssistedInject constructor(
    private val atmosphereRangeUseCase: AtmosphereRangeUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): SettingsViewModel
    }

    val lowPressure = savedStateHandle
        .getLiveData("LOW_PRESSURE", atmosphereRangeUseCase.lowPressureFlow.value)
        .asMutableStateFlow()

    val highTemp = savedStateHandle
        .getLiveData("HIGH_TEMP", atmosphereRangeUseCase.highTempFlow.value)
        .asMutableStateFlow()

    val normalTemp = savedStateHandle
        .getLiveData("NORMAL_TEMP", atmosphereRangeUseCase.normalTempFlow.value)
        .asMutableStateFlow()

    val lowTemp = savedStateHandle
        .getLiveData("LOW_TEMP", atmosphereRangeUseCase.lowTempFlow.value)
        .asMutableStateFlow()

    init {
        lowPressure
            .debounce(100.milliseconds)
            .onEach { atmosphereRangeUseCase.lowPressureFlow.value = it }
            .launchIn(viewModelScope)

        highTemp
            .debounce(100.milliseconds)
            .onEach { atmosphereRangeUseCase.highTempFlow.value = it }
            .launchIn(viewModelScope)

        normalTemp
            .debounce(100.milliseconds)
            .onEach { atmosphereRangeUseCase.normalTempFlow.value = it }
            .launchIn(viewModelScope)

        lowTemp
            .debounce(100.milliseconds)
            .onEach { atmosphereRangeUseCase.lowTempFlow.value = it }
            .launchIn(viewModelScope)
    }

    companion object
}
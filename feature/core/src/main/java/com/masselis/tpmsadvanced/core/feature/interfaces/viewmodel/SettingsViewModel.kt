package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.CarRangesUseCase
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow

internal class SettingsViewModel @AssistedInject constructor(
    carRangesUseCase: CarRangesUseCase,
    unitPreferences: UnitPreferences,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(): SettingsViewModel
    }

    val lowPressure = carRangesUseCase.lowPressure
    val highPressure = carRangesUseCase.highPressure

    val pressureUnit = unitPreferences.pressure.asStateFlow()

    val highTemp = carRangesUseCase.highTemp
    val normalTemp = carRangesUseCase.normalTemp
    val lowTemp = carRangesUseCase.lowTemp

    val temperatureUnit = unitPreferences.temperature.asStateFlow()

}

package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow

internal class VehicleSettingsViewModel @AssistedInject constructor(
    vehicleRangesUseCase: VehicleRangesUseCase,
    unitPreferences: UnitPreferences,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(): VehicleSettingsViewModel
    }

    val lowPressure = vehicleRangesUseCase.lowPressure
    val highPressure = vehicleRangesUseCase.highPressure

    val pressureUnit = unitPreferences.pressure.asStateFlow()

    val highTemp = vehicleRangesUseCase.highTemp
    val normalTemp = vehicleRangesUseCase.normalTemp
    val lowTemp = vehicleRangesUseCase.lowTemp

    val temperatureUnit = unitPreferences.temperature.asStateFlow()

}

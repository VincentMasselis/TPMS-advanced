package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

internal class VehicleSettingsViewModel @Inject constructor(
    vehicleRangesUseCase: VehicleRangesUseCase,
    unitPreferences: UnitPreferences,
) : ViewModel() {

    val lowPressure = vehicleRangesUseCase.lowPressure
    val highPressure = vehicleRangesUseCase.highPressure

    val pressureUnit = unitPreferences.pressure.asStateFlow()

    val highTemp = vehicleRangesUseCase.highTemp
    val normalTemp = vehicleRangesUseCase.normalTemp
    val lowTemp = vehicleRangesUseCase.lowTemp

    val temperatureUnit = unitPreferences.temperature.asStateFlow()

}

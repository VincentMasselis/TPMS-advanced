package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

internal class VehicleSettingsViewModelImpl @Inject constructor(
    vehicleRangesUseCase: VehicleRangesUseCase,
    unitPreferences: UnitPreferences,
) : ViewModel(), VehicleSettingsViewModel {

    override val lowPressure = vehicleRangesUseCase.lowPressure
    override val highPressure = vehicleRangesUseCase.highPressure

    override val pressureUnit = unitPreferences.pressure.asStateFlow()

    override val highTemp = vehicleRangesUseCase.highTemp
    override val normalTemp = vehicleRangesUseCase.normalTemp
    override val lowTemp = vehicleRangesUseCase.lowTemp

    override val temperatureUnit = unitPreferences.temperature.asStateFlow()

}

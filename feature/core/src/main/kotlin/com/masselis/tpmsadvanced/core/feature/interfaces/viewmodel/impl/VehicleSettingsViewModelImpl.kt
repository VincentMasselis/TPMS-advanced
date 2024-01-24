package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
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

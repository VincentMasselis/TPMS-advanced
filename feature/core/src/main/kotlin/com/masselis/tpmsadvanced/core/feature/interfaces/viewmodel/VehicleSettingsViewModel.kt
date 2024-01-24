package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal interface VehicleSettingsViewModel {
    val lowPressure: MutableStateFlow<Pressure>
    val highPressure: MutableStateFlow<Pressure>
    val pressureUnit: StateFlow<PressureUnit>

    val highTemp: MutableStateFlow<Temperature>
    val normalTemp: MutableStateFlow<Temperature>
    val lowTemp: MutableStateFlow<Temperature>

    val temperatureUnit: StateFlow<TemperatureUnit>
}

package com.masselis.tpmsadvanced.feature.unit.interfaces

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import javax.inject.Inject

internal class UnitsViewModel @Inject constructor(unitPreferences: UnitPreferences) : ViewModel() {
    val pressure = unitPreferences.pressure
    val temperature = unitPreferences.temperature
}

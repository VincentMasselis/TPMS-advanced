package com.masselis.tpmsadvanced.feature.unit.interfaces

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences

internal class UnitsViewModel(unitPreferences: UnitPreferences) : ViewModel() {
    val pressure = unitPreferences.pressure
    val temperature = unitPreferences.temperature
}

package com.masselis.tpmsadvanced.unit.interfaces

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.unit.model.PressureUnit
import com.masselis.tpmsadvanced.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.unit.usecase.UnitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class UnitsViewModel @Inject constructor(useCase: UnitUseCase) : ViewModel() {
    val pressure = useCase.pressure as MutableStateFlow<PressureUnit>
    val temperature = useCase.temperature as MutableStateFlow<TemperatureUnit>
}
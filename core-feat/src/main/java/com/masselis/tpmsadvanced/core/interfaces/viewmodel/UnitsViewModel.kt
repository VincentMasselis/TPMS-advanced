package com.masselis.tpmsadvanced.core.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.model.Pressure
import com.masselis.tpmsadvanced.core.model.Temperature
import com.masselis.tpmsadvanced.core.usecase.UnitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class UnitsViewModel @Inject constructor(useCase: UnitUseCase) : ViewModel() {
    val pressure = useCase.pressure as MutableStateFlow<Pressure.Unit>
    val temperature = useCase.temperature as MutableStateFlow<Temperature.Unit>
}
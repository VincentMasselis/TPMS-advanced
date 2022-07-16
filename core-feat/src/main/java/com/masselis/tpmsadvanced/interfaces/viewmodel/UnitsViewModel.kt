package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.usecase.UnitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class UnitsViewModel @Inject constructor(useCase: UnitUseCase) : ViewModel() {
    val pressure = useCase.pressure as MutableStateFlow<Pressure.Unit>
    val temperature = useCase.temperature as MutableStateFlow<Temperature.Unit>
}
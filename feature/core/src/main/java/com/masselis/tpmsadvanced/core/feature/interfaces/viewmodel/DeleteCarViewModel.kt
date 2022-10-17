package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CarCountUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.CarUseCase
import com.masselis.tpmsadvanced.data.car.model.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class DeleteCarViewModel @Inject constructor(
    carUseCase: CarUseCase,
    carCountUseCase: CarCountUseCase,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class DeletableCar(val car: Car) : State()
        data class LatestCar(val car: Car) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(carUseCase.carFlow(), carCountUseCase.count()) { car, count ->
            when (count) {
                1 -> State.LatestCar(car)
                else -> State.DeletableCar(car)
            }
        }.onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }
}

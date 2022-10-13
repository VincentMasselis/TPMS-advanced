package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentCarUseCase
import com.masselis.tpmsadvanced.data.car.model.Car
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class CurrentCarTextViewModel @Inject constructor(
    private val currentCarUseCase: CurrentCarUseCase,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class CurrentCar(val car: Car) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        currentCarUseCase.flow
            .flatMapLatest { it.carFlow }
            .onEach { mutableStateFlow.value = State.CurrentCar(it) }
            .launchIn(viewModelScope)
    }

    fun setCurrent(car: Car) = viewModelScope.launch {
        currentCarUseCase.setAsCurrent(car)
    }

    fun insert(carName: String) = viewModelScope.launch {
        currentCarUseCase.insertAsCurrent(carName)
    }
}

package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CarListUseCase
import com.masselis.tpmsadvanced.data.car.model.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class CarListViewModel @Inject constructor(
    carListUseCase: CarListUseCase
) : ViewModel() {

    sealed class State {
        abstract val list: List<Car>

        object Loading : State() {
            override val list: List<Car> = emptyList()
        }

        data class Cars(override val list: List<Car>) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        carListUseCase.carListFlow
            .onEach { mutableStateFlow.value = State.Cars(it) }
            .launchIn(viewModelScope)
    }
}

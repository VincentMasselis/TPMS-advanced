package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class CurrentVehicleTextViewModel @Inject constructor(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class CurrentVehicle(val vehicle: Vehicle) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        currentVehicleUseCase.flow
            .flatMapLatest { it.carFlow }
            .onEach { mutableStateFlow.value = State.CurrentVehicle(it) }
            .launchIn(viewModelScope)
    }

    fun setCurrent(vehicle: Vehicle) = viewModelScope.launch {
        currentVehicleUseCase.setAsCurrent(vehicle)
    }

    fun insert(carName: String) = viewModelScope.launch {
        currentVehicleUseCase.insertAsCurrent(carName)
    }
}

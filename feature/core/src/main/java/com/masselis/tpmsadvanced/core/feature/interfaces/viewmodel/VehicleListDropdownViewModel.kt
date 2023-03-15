package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class VehicleListDropdownViewModel @Inject constructor(
    vehicleListUseCase: VehicleListUseCase
) : ViewModel() {

    sealed class State {
        abstract val list: List<Vehicle>

        object Loading : State() {
            override val list: List<Vehicle> = emptyList()
        }

        data class Vehicles(override val list: List<Vehicle>) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        vehicleListUseCase.vehicleListFlow
            .onEach { mutableStateFlow.value = State.Vehicles(it) }
            .launchIn(viewModelScope)
    }
}

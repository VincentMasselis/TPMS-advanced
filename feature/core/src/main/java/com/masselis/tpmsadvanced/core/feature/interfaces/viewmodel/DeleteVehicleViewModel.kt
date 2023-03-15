package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleCountUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class DeleteVehicleViewModel @Inject constructor(
    vehicleUseCase: VehicleUseCase,
    vehicleCountUseCase: VehicleCountUseCase,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class DeletableVehicle(val vehicle: Vehicle) : State()
        data class LatestVehicle(val vehicle: Vehicle) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(vehicleUseCase.vehicleFlow(), vehicleCountUseCase.count()) { vehicle, count ->
            when (count) {
                1 -> State.LatestVehicle(vehicle)
                else -> State.DeletableVehicle(vehicle)
            }
        }.onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }
}

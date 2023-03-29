package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleCountUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleStateFlowUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class DeleteVehicleViewModel @AssistedInject constructor(
    private val vehicleStateFlowUseCase: VehicleStateFlowUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    vehicleCountUseCase: VehicleCountUseCase,
    @Suppress("UNUSED_PARAMETER") @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): DeleteVehicleViewModel
    }

    sealed class State {
        object Loading : State()
        data class NotDeletableVehicle(val vehicle: Vehicle) : State()
        data class DeletableVehicle(val vehicle: Vehicle) : State()
        object Leave : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(vehicleStateFlowUseCase, vehicleCountUseCase.count()) { vehicle, count ->
            when (count) {
                1 -> State.NotDeletableVehicle(vehicle)
                else -> State.DeletableVehicle(vehicle)
            }
        }.onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun delete() {
        viewModelScope.launch {
            if (stateFlow.value !is State.DeletableVehicle)
                return@launch
            deleteVehicleUseCase.delete()
            mutableStateFlow.value = State.Leave
        }
    }
}

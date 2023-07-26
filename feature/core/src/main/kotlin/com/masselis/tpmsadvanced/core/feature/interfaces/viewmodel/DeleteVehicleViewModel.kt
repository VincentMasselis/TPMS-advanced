package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleStateFlowUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class DeleteVehicleViewModel @AssistedInject constructor(
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    vehicleStateFlowUseCase: VehicleStateFlowUseCase,
    vehicleCountStateFlowUseCase: VehicleCountStateFlowUseCase,
    @Suppress("UNUSED_PARAMETER") @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): DeleteVehicleViewModel
    }

    sealed class State {
        abstract val vehicle: Vehicle

        data class NotDeletableVehicle(override val vehicle: Vehicle) : State()
        data class DeletableVehicle(override val vehicle: Vehicle) : State()
    }

    sealed class Event {
        object Leave : Event()
    }

    private val mutableStateFlow = MutableStateFlow(
        computeState(vehicleStateFlowUseCase.value, vehicleCountStateFlowUseCase.value)
    )
    val stateFlow = mutableStateFlow.asStateFlow()

    private val channel = Channel<Event>(BUFFERED)
    val eventChannel = channel as ReceiveChannel<Event>

    init {
        combine(vehicleStateFlowUseCase, vehicleCountStateFlowUseCase, ::computeState)
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun delete() {
        viewModelScope.launch {
            if (stateFlow.value !is State.DeletableVehicle)
                return@launch
            deleteVehicleUseCase.delete()
            channel.send(Event.Leave)
        }
    }

    private fun computeState(vehicle: Vehicle, count: Long) = when (count) {
        1L -> State.NotDeletableVehicle(vehicle)
        else -> State.DeletableVehicle(vehicle)
    }
}

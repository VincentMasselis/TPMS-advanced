package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.masselis.tpmsadvanced.data.car.model.Vehicle as VehicleModel

internal class DeleteVehicleAlertViewModel @Inject constructor(
    private val vehicleUseCase: VehicleUseCase,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Vehicle(val vehicle: VehicleModel) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    sealed class Event {
        object Leave : Event()
    }

    private val _evenChannel = Channel<Event>(BUFFERED)
    val eventChannel = _evenChannel as ReceiveChannel<Event>

    init {
        vehicleUseCase.vehicleFlow()
            .onEach { mutableStateFlow.value = State.Vehicle(it) }
            .launchIn(viewModelScope)
    }

    fun delete() = viewModelScope.launch {
        vehicleUseCase.delete()
        _evenChannel.send(Event.Leave)
    }
}

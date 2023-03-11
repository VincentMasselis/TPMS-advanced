package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CarUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.masselis.tpmsadvanced.data.car.model.Car as CarModel

internal class DeleteCarAlertViewModel @Inject constructor(
    private val carUseCase: CarUseCase,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Car(val car: CarModel) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    sealed class Event {
        object Leave : Event()
    }

    private val _evenChannel = Channel<Event>(BUFFERED)
    val eventChannel = _evenChannel as ReceiveChannel<Event>

    init {
        carUseCase.carFlow()
            .onEach { mutableStateFlow.value = State.Car(it) }
            .launchIn(viewModelScope)
    }

    fun delete() = viewModelScope.launch {
        carUseCase.delete()
        _evenChannel.send(Event.Leave)
    }
}

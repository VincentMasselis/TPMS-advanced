package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CarUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private val mutableEventFlow = MutableSharedFlow<Event>()
    val eventFlow = mutableEventFlow

    init {
        carUseCase.carFlow()
            .onEach { mutableStateFlow.value = State.Car(it) }
            .launchIn(viewModelScope)
    }

    fun delete() = viewModelScope.launch {
        carUseCase.delete()
        mutableEventFlow.emit(Event.Leave)
    }
}
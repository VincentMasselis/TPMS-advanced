package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.usecase.TyreAtmosphereUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class TyreViewModel @AssistedInject constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): TyreViewModel
    }

    sealed class State {
        // Shows an outlined tyre icon
        object NotDetected : State()

        // Shows a grey tyre
        data class Obsolete(val temperature: Temperature) : State()

        // Shows a blue/green/red tyre
        data class Normal(val temperature: Temperature) : State()

        // Shows a blinking blue/green/red tyre, could be an alert for the temperature or the pressure
        data class Alerting(val temperature: Temperature) : State()
    }

    private val state = savedStateHandle.getLiveData<State>("STATE", State.NotDetected)
    val stateFlow = state.asFlow()

    init {
        viewModelScope.launch {

        }
    }
}
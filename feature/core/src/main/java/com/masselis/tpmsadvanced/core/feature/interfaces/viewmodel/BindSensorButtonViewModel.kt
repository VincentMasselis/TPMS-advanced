package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentTyreBoundSensorUseCase
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
import com.masselis.tpmsadvanced.data.car.model.Sensor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalCoroutinesApi::class)
internal class BindSensorButtonViewModel @AssistedInject constructor(
    private val currentTyreBoundSensorUseCase: CurrentTyreBoundSensorUseCase,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): BindSensorButtonViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Empty : State()

        @Parcelize
        data class RequestBond(val sensor: Sensor) : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Empty)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        currentTyreBoundSensorUseCase.boundSensor
            .flatMapLatest { savedId ->
                if (savedId == null) currentTyreBoundSensorUseCase
                    .foundSensor
                    .map { State.RequestBond(it) }
                else
                    flowOf(State.Empty)
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }
}

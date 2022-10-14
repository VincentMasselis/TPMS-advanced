package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.BoundSensorUseCase
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
import com.masselis.tpmsadvanced.data.car.model.Car
import com.masselis.tpmsadvanced.data.car.model.Sensor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalCoroutinesApi::class)
internal class BindSensorDialogViewModel @AssistedInject constructor(
    private val boundSensorUseCase: BoundSensorUseCase,
    private val carFlow: Flow<Car>,
    @Assisted private val sensorToAdd: Sensor,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    internal interface Factory {
        fun build(
            sensorToAdd: Sensor,
            savedStateHandle: SavedStateHandle
        ): BindSensorDialogViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Loading : State()

        @Parcelize
        object NewSensor : State()

        @Parcelize
        data class UpdateSensor(val currentBound: Car, val newOwner: Car) : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Loading)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        boundSensorUseCase.boundCar(sensorToAdd.id)
            .flatMapLatest { boundCar ->
                if (boundCar == null) flowOf(State.NewSensor)
                else carFlow.map { State.UpdateSensor(boundCar, it) }
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun save() {
        when (stateFlow.value) {
            State.Loading -> return
            State.NewSensor, is State.UpdateSensor -> viewModelScope.launch {
                boundSensorUseCase.upsert(sensorToAdd)
            }
        }
    }
}

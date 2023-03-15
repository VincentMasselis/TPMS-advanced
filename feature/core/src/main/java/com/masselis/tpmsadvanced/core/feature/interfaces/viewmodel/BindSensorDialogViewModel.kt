package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.SensorToBindUseCase
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.car.model.Vehicle
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
    private val sensorToBindUseCase: SensorToBindUseCase,
    private val carFlow: Flow<Vehicle>,
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
        data class UpdateSensor(val currentBound: Vehicle, val newOwner: Vehicle) : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Loading)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        sensorToBindUseCase.boundVehicle(sensorToAdd)
            .flatMapLatest { boundVehicle ->
                if (boundVehicle == null) flowOf(State.NewSensor)
                else carFlow.map { State.UpdateSensor(boundVehicle, it) }
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun save() {
        when (stateFlow.value) {
            State.Loading -> return
            State.NewSensor, is State.UpdateSensor -> viewModelScope.launch {
                sensorToBindUseCase.upsert(sensorToAdd)
            }
        }
    }
}

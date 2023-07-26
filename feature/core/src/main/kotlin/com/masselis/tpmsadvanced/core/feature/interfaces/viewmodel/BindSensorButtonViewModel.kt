package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.SearchSensorUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.SensorBindingUseCase
import com.masselis.tpmsadvanced.core.ui.getMutableStateFlow
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalCoroutinesApi::class)
internal class BindSensorButtonViewModel @AssistedInject constructor(
    private val sensorBindingUseCase: SensorBindingUseCase,
    private val searchSensorUseCase: SearchSensorUseCase,
    private val currentVehicleFlow: StateFlow<Vehicle>,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): BindSensorButtonViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Empty : State()

        sealed class RequestBond : State() {
            abstract val sensor: Sensor

            @Parcelize
            data class NewBinding(override val sensor: Sensor) : RequestBond()

            @Parcelize
            data class AlreadyBound(
                override val sensor: Sensor,
                val currentVehicle: Vehicle,
                val targetVehicle: Vehicle
            ) : RequestBond()
        }
    }

    private val mutableStateFlow = savedStateHandle
        .getMutableStateFlow<State>("STATE") { State.Empty }
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        sensorBindingUseCase
            .boundSensor()
            .flatMapLatest { savedId ->
                if (savedId == null) searchSensorUseCase
                    .search()
                    .flatMapLatest { sensor ->
                        combine(
                            sensorBindingUseCase.boundVehicle(sensor),
                            currentVehicleFlow
                        ) { boundVehicle, currentVehicle ->
                            if (boundVehicle == null)
                                State.RequestBond.NewBinding(sensor)
                            else
                                State.RequestBond.AlreadyBound(sensor, boundVehicle, currentVehicle)
                        }
                    }
                else
                    flowOf(State.Empty)
            }
            .catch { emit(State.Empty) }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun bind() {
        viewModelScope.launch {
            when (val state = stateFlow.value) {
                State.Empty -> return@launch
                is State.RequestBond -> sensorBindingUseCase.bind(state.sensor)
            }
        }
    }
}

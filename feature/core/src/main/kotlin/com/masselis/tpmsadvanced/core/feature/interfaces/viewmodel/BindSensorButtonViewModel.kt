package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.SearchSensorToBindUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.SensorBindingUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.SearchSensorToBindUseCase.Result
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalCoroutinesApi::class)
internal class BindSensorButtonViewModel @AssistedInject constructor(
    private val sensorBindingUseCase: SensorBindingUseCase,
    vehicleFlow: StateFlow<Vehicle>,
    searchSensorToBindUseCase: SearchSensorToBindUseCase,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {
    @AssistedFactory
    interface Factory : (SavedStateHandle) -> BindSensorButtonViewModel

    sealed class State : Parcelable {
        @Parcelize
        data object Empty : State()

        sealed class RequestBond : State() {
            abstract val foundSensor: Sensor.Located

            @Parcelize
            data class NewBinding(override val foundSensor: Sensor.Located) : RequestBond()

            @Parcelize
            data class AlreadyBound(
                override val foundSensor: Sensor.Located,
                val currentVehicle: Vehicle,
                val targetVehicle: Vehicle
            ) : RequestBond()
        }
    }

    private val mutableStateFlow = savedStateHandle
        .getMutableStateFlow<State>("STATE") { State.Empty }
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        searchSensorToBindUseCase()
            .flatMapLatest { result ->
                when (result) {
                    is Result.AlreadyBound -> flowOf(State.Empty)

                    is Result.NewBinding -> when (result.foundSensor) {
                        is Sensor.Impl -> flowOf(State.Empty)
                        is Sensor.Located -> flowOf(State.RequestBond.NewBinding(result.foundSensor))
                    }

                    is Result.DuplicateBinding -> when (result.foundSensor) {
                        is Sensor.Impl -> flowOf(State.Empty)
                        is Sensor.Located -> vehicleFlow.map { targetVehicle ->
                            State.RequestBond.AlreadyBound(
                                result.foundSensor,
                                result.boundVehicle,
                                targetVehicle
                            )
                        }
                    }
                }
            }
            .catch { emit(State.Empty) }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun bind() {
        viewModelScope.launch {
            when (val state = stateFlow.value) {
                State.Empty -> return@launch
                is State.RequestBond -> sensorBindingUseCase.bind(state.foundSensor)
            }
        }
    }
}

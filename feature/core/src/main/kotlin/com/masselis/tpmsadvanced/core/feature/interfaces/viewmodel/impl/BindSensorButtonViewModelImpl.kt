package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel.State
import com.masselis.tpmsadvanced.core.feature.usecase.SearchSensorToBindUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.SearchSensorToBindUseCase.Result
import com.masselis.tpmsadvanced.core.feature.usecase.SensorBindingUseCase
import com.masselis.tpmsadvanced.core.ui.getMutableStateFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class BindSensorButtonViewModelImpl @AssistedInject constructor(
    private val sensorBindingUseCase: SensorBindingUseCase,
    vehicleFlow: StateFlow<Vehicle>,
    searchSensorToBindUseCase: SearchSensorToBindUseCase,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel(), BindSensorButtonViewModel {
    @AssistedFactory
    interface Factory : (SavedStateHandle) -> BindSensorButtonViewModelImpl

    private val mutableStateFlow = savedStateHandle.getMutableStateFlow<State>("STATE") {
        State.Empty
    }
    override val stateFlow = mutableStateFlow.asStateFlow()

    init {
        searchSensorToBindUseCase()
            .flatMapLatest { result ->
                when (result) {
                    is Result.AlreadyBound -> flowOf(State.Empty)

                    is Result.NewBinding -> flowOf(State.RequestBond.NewBinding(result.foundSensor))

                    is Result.DuplicateBinding -> vehicleFlow.map { targetVehicle ->
                        State.RequestBond.AlreadyBound(
                            result.foundSensor,
                            result.boundVehicle,
                            targetVehicle
                        )
                    }
                }
            }
            .catch { emit(State.Empty) }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun bind() {
        viewModelScope.launch {
            val state = stateFlow.value
            if (state !is State.RequestBond)
                return@launch
            sensorBindingUseCase.bind(state.foundSensor)
        }
    }
}

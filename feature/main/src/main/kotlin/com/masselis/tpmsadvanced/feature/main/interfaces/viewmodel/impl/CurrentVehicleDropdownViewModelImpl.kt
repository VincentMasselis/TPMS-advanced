package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.CurrentVehicleDropdownViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.CurrentVehicleDropdownViewModel.State
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AssistedInject
internal class CurrentVehicleDropdownViewModelImpl(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    vehicleListUseCase: VehicleListUseCase,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel(), CurrentVehicleDropdownViewModel {

    @AssistedFactory
    interface Factory : ViewModelAssistedFactory {
        operator fun invoke(savedStateHandle: SavedStateHandle): CurrentVehicleDropdownViewModelImpl

        override fun create(extras: CreationExtras): CurrentVehicleDropdownViewModelImpl =
            invoke(extras.createSavedStateHandle())
    }

    private val mutableStateFlow = savedStateHandle.getMutableStateFlow<State>(
        "STATE",
        State.Loading
    )
    override val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(
            currentVehicleUseCase.flatMapLatest { it.vehicleStateFlow },
            vehicleListUseCase.vehicleListFlow
        ) { current, list ->
            State.Vehicles(current, list.sortedBy { it.name })
        }.onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun setCurrent(vehicle: Vehicle) {
        viewModelScope.launch {
            currentVehicleUseCase.setAsCurrent(vehicle)
        }
    }

    override fun insert(carName: String, kind: Vehicle.Kind) {
        viewModelScope.launch {
            currentVehicleUseCase.insertAsCurrent(carName, kind)
        }
    }
}

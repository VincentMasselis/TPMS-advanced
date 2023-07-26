package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.core.ui.getMutableStateFlow
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalCoroutinesApi::class)
internal class CurrentVehicleDropdownViewModel @AssistedInject constructor(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    vehicleListUseCase: VehicleListUseCase,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): CurrentVehicleDropdownViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Loading : State()

        @Parcelize
        data class Vehicles(
            val current: Vehicle,
            val list: List<Vehicle>,
        ) : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getMutableStateFlow<State>("STATE") { State.Loading }
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(
            currentVehicleUseCase.flatMapLatest { it.carFlow },
            vehicleListUseCase.vehicleListFlow
        ) { current, list ->
            State.Vehicles(current, list.sortedBy { it.name })
        }.onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun setCurrent(vehicle: Vehicle) = viewModelScope.launch {
        currentVehicleUseCase.setAsCurrent(vehicle)
    }

    fun insert(carName: String, kind: Vehicle.Kind) = viewModelScope.launch {
        currentVehicleUseCase.insertAsCurrent(carName, kind)
    }
}

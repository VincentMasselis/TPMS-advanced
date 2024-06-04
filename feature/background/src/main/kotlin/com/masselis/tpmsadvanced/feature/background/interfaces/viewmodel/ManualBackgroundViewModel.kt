package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.ui.getMutableStateFlow
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal class ManualBackgroundViewModel @AssistedInject constructor(
    private val checkForPermissionUseCase: CheckForPermissionUseCase,
    private val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase,
    @Assisted private val vehicle: Vehicle,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    internal interface Factory : (Vehicle, SavedStateHandle) -> ManualBackgroundViewModel

    sealed class State : Parcelable {
        @Parcelize
        data object Loading : State()

        @Parcelize
        data object Idle : State()

        @Parcelize
        data object Monitoring : State()
    }

    private val mutableStateFlow = savedStateHandle.getMutableStateFlow<State>("STATE") {
        State.Loading
    }
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        vehiclesToMonitorUseCase
            .ignoredAndMonitored()
            .map { (_, monitored) ->
                monitored
                    .map { it.uuid }
                    .contains(vehicle.uuid)
            }
            .map { currentIsMonitored ->
                if (currentIsMonitored) State.Monitoring
                else State.Idle
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun missingPermission() = checkForPermissionUseCase.missingPermission()

    fun monitor() = viewModelScope.launch {
        vehiclesToMonitorUseCase.enableManual(vehicle.uuid)
    }

    fun disableMonitoring() = viewModelScope.launch {
        vehiclesToMonitorUseCase.disableManual(vehicle.uuid)
    }
}

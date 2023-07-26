package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
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
    internal interface Factory {
        fun build(
            vehicle: Vehicle,
            savedStateHandle: SavedStateHandle,
        ): ManualBackgroundViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Loading : State()

        @Parcelize
        object Idle : State()

        @Parcelize
        object Monitoring : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Loading)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        vehiclesToMonitorUseCase
            .expectedIgnoredAndMonitored()
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

    fun requiredPermission() = checkForPermissionUseCase.requiredPermission

    fun isPermissionGrant() = checkForPermissionUseCase.isPermissionGrant()

    fun monitor() = viewModelScope.launch {
        vehiclesToMonitorUseCase.enableManual(vehicle.uuid)
    }

    fun disableMonitoring() = viewModelScope.launch {
        vehiclesToMonitorUseCase.disableManual(vehicle.uuid)
    }
}

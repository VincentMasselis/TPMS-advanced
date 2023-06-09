package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal class ManualBackgroundViewModel @AssistedInject constructor(
    private val checkForPermissionUseCase: CheckForPermissionUseCase,
    private val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    internal interface Factory {
        fun build(savedStateHandle: SavedStateHandle): ManualBackgroundViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Enable : State()

        @Parcelize
        object Disable : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Disable)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(
            currentVehicleUseCase
                .flow,
            vehiclesToMonitorUseCase
                .expectedIgnoredAndMonitored()
                .map { (_, monitored) -> monitored }
        ) { current, monitored -> monitored.map { it.uuid }.contains(current.vehicle.uuid) }
            .map { currentIsMonitored ->
                if (currentIsMonitored) State.Disable
                else State.Enable
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun requiredPermission() = checkForPermissionUseCase.requiredPermission

    fun isPermissionGrant() = checkForPermissionUseCase.isPermissionGrant()

    fun monitor() = viewModelScope.launch {
        vehiclesToMonitorUseCase.enableManual(currentVehicleUseCase.flow.first().vehicle.uuid)
    }
}

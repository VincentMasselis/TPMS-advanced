package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    fun requiredPermission() = checkForPermissionUseCase.requiredPermission

    fun isPermissionGrant() = checkForPermissionUseCase.isPermissionGrant()

    fun monitor() = viewModelScope.launch {
        vehiclesToMonitorUseCase.enableManual(currentVehicleUseCase.flow.first().vehicle.uuid)
    }
}

package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class ManualBackgroundViewModel @AssistedInject constructor(
    private val checkForPermissionUseCase: CheckForPermissionUseCase,
    private val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase,
    @Assisted private val vehicle: Vehicle,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    internal interface Factory {
        fun build(vehicle: Vehicle, savedStateHandle: SavedStateHandle): ManualBackgroundViewModel
    }

    fun requiredPermission() = checkForPermissionUseCase.requiredPermission

    fun isPermissionGrant() = checkForPermissionUseCase.isPermissionGrant()

    fun monitor() = vehiclesToMonitorUseCase.enableManual(vehicle.uuid)
}

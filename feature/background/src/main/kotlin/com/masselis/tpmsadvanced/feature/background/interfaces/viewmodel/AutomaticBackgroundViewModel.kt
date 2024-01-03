package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.database.asOneChillFlow
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.skip
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal class AutomaticBackgroundViewModel @AssistedInject constructor(
    private val database: VehicleDatabase,
    private val checkForPermissionUseCase: CheckForPermissionUseCase,
    @Assisted private val vehicle: Vehicle
) : ViewModel() {

    @AssistedFactory
    internal interface Factory : (Vehicle) -> AutomaticBackgroundViewModel

    sealed class State : Parcelable {
        @Parcelize
        data object MonitorDisabled : State()

        @Parcelize
        data object MonitorEnabled : State()
    }

    private val mutableStateFlow: MutableStateFlow<State>
    val stateFlow: StateFlow<State>

    init {
        database.selectIsBackgroundMonitor(vehicle.uuid)
            .apply {
                mutableStateFlow = MutableStateFlow(computeState(executeAsOne()))
                stateFlow = mutableStateFlow.asStateFlow()
            }
            .asOneChillFlow()
            .map { computeState(it) }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun requiredPermission() = checkForPermissionUseCase.requiredPermission

    fun isPermissionGrant() = checkForPermissionUseCase.isPermissionGrant()

    fun monitor() {
        assert(checkForPermissionUseCase.isPermissionGrant())
        viewModelScope.launch {
            database.updateIsBackgroundMonitor(true, vehicle.uuid)
        }
    }

    fun disable() = viewModelScope.launch {
        database.updateIsBackgroundMonitor(false, vehicle.uuid)
    }

    private fun computeState(isMonitoring: Boolean) =
        if (isMonitoring) State.MonitorEnabled
        else State.MonitorDisabled
}

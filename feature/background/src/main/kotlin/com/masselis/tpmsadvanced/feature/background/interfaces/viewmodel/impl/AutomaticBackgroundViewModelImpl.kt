package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.AutomaticBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.AutomaticBackgroundViewModel.State
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class AutomaticBackgroundViewModelImpl @AssistedInject constructor(
    private val database: VehicleDatabase,
    private val checkForPermissionUseCase: CheckForPermissionUseCase,
    @Assisted private val vehicle: Vehicle
) : ViewModel(), AutomaticBackgroundViewModel {

    @AssistedFactory
    internal interface Factory : (Vehicle) -> AutomaticBackgroundViewModelImpl

    private val mutableStateFlow: MutableStateFlow<State>
    override val stateFlow: StateFlow<State>

    init {
        database.selectIsBackgroundMonitor(vehicle.uuid)
            .apply {
                mutableStateFlow = MutableStateFlow(computeState(execute()))
                stateFlow = mutableStateFlow.asStateFlow()
            }
            .asChillFlow()
            .map { computeState(it) }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun missingPermission(): String? = checkForPermissionUseCase.missingPermission()

    override fun monitor() {
        assert(checkForPermissionUseCase.isGrant())
        viewModelScope.launch {
            database.updateIsBackgroundMonitor(true, vehicle.uuid)
        }
    }

    override fun disable() {
        viewModelScope.launch {
            database.updateIsBackgroundMonitor(false, vehicle.uuid)
        }
    }

    private fun computeState(isMonitoring: Boolean) =
        if (isMonitoring) State.MonitorEnabled
        else State.MonitorDisabled
}

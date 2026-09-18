package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow.State
import kotlinx.coroutines.flow.StateFlow

internal interface TyreStatsViewModel {
    val stateFlow: StateFlow<State>
    val showTimestamp: StateFlow<Boolean>
    val showSensorId: StateFlow<Boolean>
    val showTimeSinceUpdate: StateFlow<Boolean>
}

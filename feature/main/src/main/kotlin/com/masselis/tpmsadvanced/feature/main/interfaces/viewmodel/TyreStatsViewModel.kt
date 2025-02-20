package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow.State
import kotlinx.coroutines.flow.StateFlow

internal interface TyreStatsViewModel {
    val stateFlow: StateFlow<State>
}

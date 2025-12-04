package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow
import kotlinx.coroutines.flow.StateFlow

internal interface TyreIconViewModel {
     val stateFlow: StateFlow<TyreIconStateFlow.State>
}

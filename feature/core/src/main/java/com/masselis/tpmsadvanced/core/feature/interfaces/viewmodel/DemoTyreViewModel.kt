package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class DemoTyreViewModel(state: State) : TyreViewModel {
    override val stateFlow: StateFlow<State> = MutableStateFlow(state)
}

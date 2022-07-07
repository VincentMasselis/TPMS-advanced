package com.masselis.tpmsadvanced.interfaces.viewmodel

import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DemoTyreViewModel(private val state: State) : TyreViewModel {
    override val stateFlow: StateFlow<State> = MutableStateFlow(state)
}
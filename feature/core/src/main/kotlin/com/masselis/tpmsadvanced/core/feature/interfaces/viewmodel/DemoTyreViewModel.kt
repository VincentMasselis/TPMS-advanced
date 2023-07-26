package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class DemoTyreViewModel(state: State) : ViewModel(), TyreViewModel {
    override val stateFlow: StateFlow<State> = MutableStateFlow(state)
}

package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.ClearBoundSensorsViewModel.State
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.ClearBoundSensorsViewModel.State.AlreadyCleared
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.ClearBoundSensorsViewModel.State.ClearingPossible
import com.masselis.tpmsadvanced.feature.main.usecase.ClearBoundSensorsUseCase
import com.masselis.tpmsadvanced.core.ui.getMutableStateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class ClearBoundSensorsViewModelImpl @AssistedInject constructor(
    private val clearBoundSensorsUseCase: ClearBoundSensorsUseCase,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel(), ClearBoundSensorsViewModel {

    @AssistedFactory
    interface Factory : (SavedStateHandle) -> ClearBoundSensorsViewModelImpl

    private val mutableStateFlow = savedStateHandle.getMutableStateFlow<State>("STATE") {
        AlreadyCleared
    }
    override val stateFlow = mutableStateFlow.asStateFlow()

    init {
        clearBoundSensorsUseCase.isClearingAllowed()
            .map { isAllowed -> if (isAllowed) ClearingPossible else AlreadyCleared }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun clear() {
        viewModelScope.launch {
            if (stateFlow.value !is ClearingPossible)
                return@launch
            clearBoundSensorsUseCase.clear()
        }
    }
}

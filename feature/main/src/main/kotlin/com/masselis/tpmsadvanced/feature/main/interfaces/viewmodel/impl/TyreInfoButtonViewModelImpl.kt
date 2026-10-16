package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.vehicle.model.SuspiciousTeslaFixedTemperature
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreInfoButtonViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreInfoButtonViewModel.State
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreUseCase
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AssistedInject
internal class TyreInfoButtonViewModelImpl(
    listenTyreUseCase: ListenTyreUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle,
) : ViewModel(), TyreInfoButtonViewModel {

    @AssistedFactory
    interface Factory {
        operator fun invoke(savedStateHandle: SavedStateHandle): TyreInfoButtonViewModelImpl
    }

    private val mutableStateFlow = savedStateHandle.getMutableStateFlow<State>(
        "STATE",
        State.AllClear
    )
    override val stateFlow: StateFlow<State> = mutableStateFlow.asStateFlow()

    init {
        listenTyreUseCase.listen()
            .map { tyre ->
                if (tyre.infos.any { it is SuspiciousTeslaFixedTemperature })
                    State.SuspiciousTeslaFixedTemperature
                else
                    State.AllClear
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }
}

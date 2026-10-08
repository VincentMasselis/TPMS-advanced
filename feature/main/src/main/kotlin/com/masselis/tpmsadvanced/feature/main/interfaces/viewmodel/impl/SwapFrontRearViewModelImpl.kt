package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.SwapFrontRearViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.SwapFrontRearViewModel.Event
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.SwapFrontRearViewModel.State
import com.masselis.tpmsadvanced.feature.main.usecase.SwapFrontRearUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.seconds

internal class SwapFrontRearViewModelImpl(
    private val swapFrontRearUseCase: SwapFrontRearUseCase,
) : ViewModel(), SwapFrontRearViewModel {


    private val mutableStateFlow = MutableStateFlow<State>(State.Unavailable)
    override val stateFlow = mutableStateFlow.asStateFlow()

    override val eventChannel = Channel<Event>()

    init {
        combine(
            swapFrontRearUseCase.canSwap(),
            swapFrontRearUseCase.lastSwap()
        ) { canSwap, lastSwap ->
            when {
                canSwap.not() -> State.Unavailable
                lastSwap != null -> State.LastUsage(lastSwap)
                else -> State.NeverUsed
            }
        }.onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun swap() {
        viewModelScope.launch {
            val state = stateFlow.value
            if (state == State.Unavailable)
                return@launch
            if (state is State.LastUsage && now().minus(state.instant) < 5.seconds)
                return@launch

            swapFrontRearUseCase.swap()
            eventChannel.send(Event.SensorsSwapped)
        }
    }
}
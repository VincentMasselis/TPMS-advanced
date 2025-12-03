package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

internal class VehicleHomeViewModel(
    noveltyUseCase: NoveltyUseCase,
) : ViewModel() {

    sealed class SpotlightEvent {
        data object ManualMonitorDropdown : SpotlightEvent()
    }

    private val channel = Channel<SpotlightEvent>(BUFFERED)
    val eventChannel = channel as ReceiveChannel<SpotlightEvent>

    init {
        viewModelScope.launch {
            @Suppress("MagicNumber")
            if (noveltyUseCase.consume("manual_monitor", 1022L))
                channel.send(SpotlightEvent.ManualMonitorDropdown)
        }
    }
}

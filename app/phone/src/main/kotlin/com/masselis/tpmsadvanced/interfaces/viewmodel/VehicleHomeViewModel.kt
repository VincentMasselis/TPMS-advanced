package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

@Suppress("MagicNumber")
internal class VehicleHomeViewModel(
    noveltyUseCase: NoveltyUseCase,
) : ViewModel() {

    sealed class Event {
        data object ManualMonitorDropdown : Event()

        data object WicarlinkSupport: Event()
    }

    private val channel = Channel<Event>(BUFFERED)
    val eventChannel = channel as ReceiveChannel<Event>

    init {
        viewModelScope.launch {
            if(noveltyUseCase.consume("wicarlink_support", 1_06_00_000L..1_06_00_999L)) {
                channel.send(Event.WicarlinkSupport)
                return@launch
            }
            if (noveltyUseCase.consume("manual_monitor", 1022L..1022L)) {
                channel.send(Event.ManualMonitorDropdown)
                return@launch
            }
        }
    }
}

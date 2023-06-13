package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.NoveltyUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

internal class HomeViewModel @AssistedInject constructor(
    noveltyUseCase: NoveltyUseCase,
    @Suppress("UnusedPrivateMember") @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): HomeViewModel
    }

    sealed class SpotlightEvent {
        object CarListDropdown : SpotlightEvent()
    }

    private val channel = Channel<SpotlightEvent>(BUFFERED)
    val eventChannel = channel as ReceiveChannel<SpotlightEvent>

    init {
        viewModelScope.launch {
            @Suppress("MagicNumber")
            if (noveltyUseCase.consume("car_kind", 1020L))
                channel.send(SpotlightEvent.CarListDropdown)
        }
    }

}

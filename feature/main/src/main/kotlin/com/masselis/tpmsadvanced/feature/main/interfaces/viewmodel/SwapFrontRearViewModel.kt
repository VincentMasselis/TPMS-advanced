package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import android.os.Parcelable
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import kotlin.time.Instant

internal interface SwapFrontRearViewModel {
    @Parcelize
    sealed interface State : Parcelable {
        @Parcelize
        data object Unavailable : State

        @Parcelize
        data object NeverUsed : State

        @Parcelize
        @JvmInline
        value class LastUsage(val instant: Instant) : State
    }

    sealed interface Event {
        data object SensorsSwapped : Event
    }

    val stateFlow: StateFlow<State>
    val eventChannel: ReceiveChannel<Event>
    fun swap()
}

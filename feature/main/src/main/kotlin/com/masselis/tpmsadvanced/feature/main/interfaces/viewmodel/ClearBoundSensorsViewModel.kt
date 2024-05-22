package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import android.os.Parcelable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface ClearBoundSensorsViewModel {
    sealed class State : Parcelable {
        @Parcelize
        data object ClearingPossible : State()

        @Parcelize
        data object AlreadyCleared : State()
    }

    val stateFlow: StateFlow<State>

    fun clear()
}

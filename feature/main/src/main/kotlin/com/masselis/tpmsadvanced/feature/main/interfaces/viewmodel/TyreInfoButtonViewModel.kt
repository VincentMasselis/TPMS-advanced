package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import android.os.Parcelable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface TyreInfoButtonViewModel {
    sealed interface State : Parcelable {
        @Parcelize
        data object AllClear : State

        @Parcelize
        data object SuspiciousTeslaFixedTemperature : State
    }

    val stateFlow: StateFlow<State>
}

package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import android.os.Parcelable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface AutomaticBackgroundViewModel {
    sealed class State : Parcelable {
        @Parcelize
        data object MonitorDisabled : State()

        @Parcelize
        data object MonitorEnabled : State()
    }

    val stateFlow: StateFlow<State>
    fun requiredPermission(): String?
    fun isPermissionGrant(): Boolean
    fun monitor()
    fun disable()
}

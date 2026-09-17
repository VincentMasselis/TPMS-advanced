package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize

internal class DemoModeSwitchViewModel(
    private val demoOrBleScannerUc: DemoOrBleScannerUseCase
) : ViewModel() {
    sealed interface State : Parcelable {
        @Parcelize
        data object Disabled : State

        @Parcelize
        data object Enabled : State
    }

    private val mutableStateFlow = MutableStateFlow(computeState(demoOrBleScannerUc.isDemo.value))
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        demoOrBleScannerUc.isDemo
            .map(::computeState)
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun enable() {
        if (stateFlow.value != State.Disabled) return
        demoOrBleScannerUc.demo()
    }

    fun disable() {
        if (stateFlow.value != State.Enabled) return
        demoOrBleScannerUc.ble()
    }

    private fun computeState(isDemo: Boolean) =
        if (isDemo) State.Enabled
        else State.Disabled
}

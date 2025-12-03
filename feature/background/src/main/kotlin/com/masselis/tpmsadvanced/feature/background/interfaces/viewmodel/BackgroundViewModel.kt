package com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Parcelable
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService.Companion.intent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal class BackgroundViewModel(
    vehicle: Vehicle,
) : ViewModel() {

    sealed interface State : Parcelable {
        @Parcelize
        data object Idle : State

        @Parcelize
        data object Monitoring : State
    }

    sealed interface Event {
        data object FinishActivity : Event
    }

    private val mutableStateFlow = MutableStateFlow(computeState(MonitorService.isRunning.value))
    val stateFlow = mutableStateFlow.asStateFlow()

    private val channel = Channel<Event>(BUFFERED)
    val eventChannel = channel as ReceiveChannel<Event>

    private val serviceIntent = intent(vehicle)

    init {
        MonitorService
            .isRunning
            .map(::computeState)
            .onEach(mutableStateFlow::value::set)
            .launchIn(viewModelScope)
    }

    fun monitor() = viewModelScope.launch {
        require(stateFlow.value is State.Idle)
        if (SDK_INT >= TIRAMISU)
            require(checkSelfPermission(appContext, POST_NOTIFICATIONS) == PERMISSION_GRANTED)
        startForegroundService(appContext, serviceIntent)
        channel.send(Event.FinishActivity)
    }

    fun disableMonitoring() = viewModelScope.launch {
        require(stateFlow.value is State.Monitoring)
        appContext.stopService(serviceIntent)
    }

    private fun computeState(isServiceRunning: Boolean) =
        if (isServiceRunning) State.Monitoring
        else State.Idle
}

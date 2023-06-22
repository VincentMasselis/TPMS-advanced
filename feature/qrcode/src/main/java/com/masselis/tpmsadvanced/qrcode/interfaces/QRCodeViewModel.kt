package com.masselis.tpmsadvanced.qrcode.interfaces

import androidx.camera.view.CameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.qrcode.model.SensorMap
import com.masselis.tpmsadvanced.qrcode.usecase.BoundSensorMapUseCase
import com.masselis.tpmsadvanced.qrcode.usecase.QrCodeAnalyserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class QRCodeViewModel @AssistedInject constructor(
    private val qrCodeAnalyserUseCase: QrCodeAnalyserUseCase,
    private val boundSensorMapUseCase: BoundSensorMapUseCase,
    @Assisted private val controller: CameraController
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(controller: CameraController): QRCodeViewModel
    }

    sealed class State {
        object Scanning : State()
        data class AskForBinding(val sensorMap: SensorMap) : State()
    }

    sealed class Event {
        object Leave : Event()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Scanning)
    val stateFlow = mutableStateFlow.asStateFlow()

    private val channel = Channel<Event>(BUFFERED)
    val receiveChannel: ReceiveChannel<Event> = channel

    init {
        stateFlow
            .flatMapLatest {
                when (it) {
                    is State.AskForBinding -> emptyFlow()
                    State.Scanning -> qrCodeAnalyserUseCase.analyse(controller)
                }
            }
            .onEach { mutableStateFlow.value = State.AskForBinding(it) }
            .launchIn(viewModelScope)
    }

    fun bindSensors(ids: SensorMap) = viewModelScope.launch {
        boundSensorMapUseCase.bind(ids)
        channel.send(Event.Leave)
    }

    fun scanAgain() {
        mutableStateFlow.value = State.Scanning
    }
}

package com.masselis.tpmsadvanced.qrcode.interfaces

import androidx.camera.view.CameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.*
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class QRCodeViewModel @AssistedInject constructor(
    private val qrCodeAnalyserUseCase: QrCodeAnalyserUseCase,
    private val boundSensorMapUseCase: BoundSensorMapUseCase,
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    @Assisted private val controller: CameraController
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(controller: CameraController): QRCodeViewModel
    }

    sealed class State {
        object Scanning : State()

        sealed class AskForBinding : State() {
            abstract val sensorMap: SensorMap

            data class Compatible(override val sensorMap: SensorMap) : AskForBinding()

            data class Missing(
                override val sensorMap: SensorMap,
                val localisations: Set<Vehicle.ManySensor>
            ) : AskForBinding()
        }
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
            .flatMapLatest { state ->
                when (state) {
                    is State.AskForBinding -> emptyFlow()
                    State.Scanning -> qrCodeAnalyserUseCase
                        .analyse(controller)
                        .flatMapLatest { sensorMap ->
                            currentVehicleUseCase
                                .map { it.vehicle.kind }
                                .distinctUntilChanged()
                                .map { vehicleKind ->
                                    val missing = vehicleKind.locations
                                        .subtract(vehicleKind.computeLocations(sensorMap.keys))
                                    if (missing.isEmpty())
                                        State.AskForBinding.Compatible(sensorMap)
                                    else
                                        State.AskForBinding.Missing(sensorMap, missing)
                                }
                        }
                }
            }.onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun bindSensors() = viewModelScope.launch {
        val state = mutableStateFlow.value
        if (state !is State.AskForBinding)
            return@launch
        boundSensorMapUseCase.bind(state.sensorMap)
        channel.send(Event.Leave)
    }

    fun scanAgain() {
        mutableStateFlow.value = State.Scanning
    }
}

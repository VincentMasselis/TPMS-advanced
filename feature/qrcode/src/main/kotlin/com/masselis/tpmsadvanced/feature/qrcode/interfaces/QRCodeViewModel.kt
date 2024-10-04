package com.masselis.tpmsadvanced.feature.qrcode.interfaces

import androidx.camera.view.CameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
import com.masselis.tpmsadvanced.feature.qrcode.model.QrCodeSensors
import com.masselis.tpmsadvanced.feature.qrcode.usecase.BoundSensorMapUseCase
import com.masselis.tpmsadvanced.feature.qrcode.usecase.QrCodeSensorUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class QRCodeViewModel @AssistedInject constructor(
    private val qrCodeSensorUseCase: QrCodeSensorUseCase,
    private val boundSensorMapUseCase: BoundSensorMapUseCase,
    @Assisted private val controller: CameraController
) : ViewModel() {

    @AssistedFactory
    interface Factory : (CameraController) -> QRCodeViewModel

    sealed interface State {
        data object Scanning : State

        sealed interface AskForBinding : State {
            val qrCodeSensors: QrCodeSensors

            @JvmInline
            value class Compatible(override val qrCodeSensors: QrCodeSensors) : AskForBinding

            data class Missing(
                override val qrCodeSensors: QrCodeSensors,
                val locations: Set<Vehicle.Kind.Location>
            ) : AskForBinding
        }

        sealed interface Error : State {
            @JvmInline
            value class DuplicateWheelLocation(val wheels: Collection<Wheel>) : Error

            @JvmInline
            value class DuplicateId(val ids: Collection<Int>) : Error
        }
    }

    sealed class Event {
        data object LeaveBecauseCameraUnavailable : Event()
        data object Leave : Event()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Scanning)
    val stateFlow = mutableStateFlow.asStateFlow()

    private val channel = Channel<Event>(BUFFERED)
    val eventChannel: ReceiveChannel<Event> = channel

    init {
        stateFlow
            .flatMapLatest { state ->
                when (state) {
                    is State.AskForBinding, is State.Error -> emptyFlow()

                    State.Scanning -> qrCodeSensorUseCase
                        .analyse(controller)
                        .map { (sensors, missingLocations) ->
                            if (missingLocations.isEmpty())
                                State.AskForBinding.Compatible(sensors)
                            else
                                State.AskForBinding.Missing(sensors, missingLocations) as State
                        }
                        .catch { exc ->
                            when (exc) {
                                is CameraAnalyser.CameraUnavailable ->
                                    channel.send(Event.LeaveBecauseCameraUnavailable)

                                is QrCodeSensors.DuplicateWheelLocation -> exc
                                    .wheels
                                    .duplicates()
                                    .let(State.Error::DuplicateWheelLocation)
                                    .also { emit(it) }

                                is QrCodeSensors.DuplicateId -> exc
                                    .ids
                                    .duplicates()
                                    .let(State.Error::DuplicateId)
                                    .also { emit(it) }

                                else -> throw exc
                            }
                        }
                }
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun bindSensors() = viewModelScope.launch {
        val state = mutableStateFlow.value
        if (state !is State.AskForBinding)
            return@launch
        boundSensorMapUseCase.bind(state.qrCodeSensors)
        channel.send(Event.Leave)
    }

    fun scanAgain() {
        mutableStateFlow.value = State.Scanning
    }

    private fun <T> Iterable<T>.duplicates() = groupingBy { it }
        .eachCount()
        .mapNotNull { (value, count) -> if (count > 1) value else null }
}

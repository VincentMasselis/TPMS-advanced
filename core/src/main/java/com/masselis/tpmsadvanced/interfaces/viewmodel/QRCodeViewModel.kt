package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.camera.view.CameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.model.SensorIds
import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.usecase.QrCodeAnalyserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors

@OptIn(ExperimentalCoroutinesApi::class)
class QRCodeViewModel @AssistedInject constructor(
    private val qrCodeAnalyserUseCase: QrCodeAnalyserUseCase,
    @Assisted private val controller: CameraController
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(controller: CameraController): QRCodeViewModel
    }

    sealed class State {
        object Scanning : State()
        data class AskFavourites(val sensorIds: SensorIds) : State()
        object Leave : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Scanning)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        stateFlow
            .flatMapLatest {
                when (it) {
                    State.Leave, is State.AskFavourites -> emptyFlow()
                    State.Scanning -> qrCodeAnalyserUseCase.analyse(controller)
                }
            }
            .onEach { mutableStateFlow.value = State.AskFavourites(it) }
            .launchIn(viewModelScope)
    }

    fun addToFavourites(ids: SensorIds) {
        TyreLocation.FRONT_LEFT.component.favouriteSensorUseCase.savedId.value = ids.frontLeft
        TyreLocation.FRONT_RIGHT.component.favouriteSensorUseCase.savedId.value = ids.frontRight
        TyreLocation.REAR_LEFT.component.favouriteSensorUseCase.savedId.value = ids.rearLeft
        TyreLocation.REAR_RIGHT.component.favouriteSensorUseCase.savedId.value = ids.rearRight
        mutableStateFlow.value = State.Leave
    }

    fun scanAgain() {
        mutableStateFlow.value = State.Scanning
    }
}
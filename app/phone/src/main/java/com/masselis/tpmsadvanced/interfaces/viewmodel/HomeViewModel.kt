package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.NoveltyUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

internal class HomeViewModel @Inject constructor(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    noveltyUseCase: NoveltyUseCase,
) : ViewModel() {

    sealed class SpotlightEvent {
        object ManualMonitorDropdown : SpotlightEvent()
    }

    private val channel = Channel<SpotlightEvent>(BUFFERED)
    val eventChannel = channel as ReceiveChannel<SpotlightEvent>

    val vehicleComponentStateFlow: StateFlow<VehicleComponent> = currentVehicleUseCase

    init {
        viewModelScope.launch {
            @Suppress("MagicNumber")
            if (noveltyUseCase.consume("manual_monitor", 1022L))
                channel.send(SpotlightEvent.ManualMonitorDropdown)
        }
    }

    fun setCurrentVehicle(uuid: UUID) = viewModelScope.launch {
        currentVehicleUseCase.setAsCurrent(uuid)
    }

}

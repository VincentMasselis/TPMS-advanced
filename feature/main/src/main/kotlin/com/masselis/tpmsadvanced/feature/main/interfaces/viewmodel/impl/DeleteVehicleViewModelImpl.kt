package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.common.combineStates
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DeleteVehicleViewModel.Event
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DeleteVehicleViewModel.State
import com.masselis.tpmsadvanced.feature.main.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleStateFlowUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

internal class DeleteVehicleViewModelImpl(
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    vehicleStateFlowUseCase: VehicleStateFlowUseCase,
    vehicleCountStateFlowUseCase: VehicleCountStateFlowUseCase,
) : ViewModel(), DeleteVehicleViewModel {

    override val stateFlow = combineStates(
        vehicleStateFlowUseCase,
        vehicleCountStateFlowUseCase
    ) { vehicle, count ->
        when (count) {
            1L -> State.NotDeletableVehicle(vehicle)
            else -> State.DeletableVehicle(vehicle)
        }
    }

    private val channel = Channel<Event>(BUFFERED)
    override val eventChannel = channel as ReceiveChannel<Event>

    override fun delete() {
        viewModelScope.launch {
            if (stateFlow.value !is State.DeletableVehicle)
                return@launch
            deleteVehicleUseCase.delete()
            channel.send(Event.Leave)
        }
    }
}

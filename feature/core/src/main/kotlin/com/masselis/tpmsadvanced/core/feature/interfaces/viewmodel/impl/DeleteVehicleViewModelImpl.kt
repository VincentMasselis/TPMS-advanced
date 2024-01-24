package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel.Event
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel.State
import com.masselis.tpmsadvanced.core.feature.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleStateFlowUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class DeleteVehicleViewModelImpl @Inject constructor(
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    vehicleStateFlowUseCase: VehicleStateFlowUseCase,
    vehicleCountStateFlowUseCase: VehicleCountStateFlowUseCase,
) : ViewModel(), DeleteVehicleViewModel {


    private val mutableStateFlow = MutableStateFlow(
        computeState(vehicleStateFlowUseCase.value, vehicleCountStateFlowUseCase.value)
    )
    override val stateFlow = mutableStateFlow.asStateFlow()

    private val channel = Channel<Event>(BUFFERED)
    override val eventChannel = channel as ReceiveChannel<Event>

    init {
        combine(vehicleStateFlowUseCase, vehicleCountStateFlowUseCase, ::computeState)
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun delete() {
        viewModelScope.launch {
            if (stateFlow.value !is State.DeletableVehicle)
                return@launch
            deleteVehicleUseCase.delete()
            channel.send(Event.Leave)
        }
    }

    private fun computeState(vehicle: Vehicle, count: Long) = when (count) {
        1L -> State.NotDeletableVehicle(vehicle)
        else -> State.DeletableVehicle(vehicle)
    }
}

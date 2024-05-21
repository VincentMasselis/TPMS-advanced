package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel

import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.StateFlow

internal interface DeleteVehicleViewModel {
    sealed class State {
        abstract val vehicle: Vehicle

        data class NotDeletableVehicle(override val vehicle: Vehicle) : State()
        data class DeletableVehicle(override val vehicle: Vehicle) : State()
    }

    sealed class Event {
        data object Leave : Event()
    }

    val stateFlow: StateFlow<State>
    val eventChannel: ReceiveChannel<Event>

    fun delete()
}

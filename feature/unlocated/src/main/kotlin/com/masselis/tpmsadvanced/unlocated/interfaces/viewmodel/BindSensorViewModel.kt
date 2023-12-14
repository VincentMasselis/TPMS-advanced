package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface BindSensorViewModel {
    sealed interface State : Parcelable {

        val currentVehicle: Vehicle
        val alreadyBoundLocations: Set<Vehicle.Kind.Location>

        @Parcelize
        data class ReadyToBind(
            override val currentVehicle: Vehicle,
            override val alreadyBoundLocations: Set<Vehicle.Kind.Location>,
        ) : State

        @Parcelize
        data class BoundToAnOtherVehicle(
            override val currentVehicle: Vehicle,
            override val alreadyBoundLocations: Set<Vehicle.Kind.Location>,
            val boundVehicle: Vehicle,
            val boundVehicleLocation: Vehicle.Kind.Location,
        ) : State
    }

    val stateFlow: StateFlow<State>

    fun bind(location: Vehicle.Kind.Location)
}
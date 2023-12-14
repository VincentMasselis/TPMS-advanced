package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface BindSensorViewModel {
    sealed interface State : Parcelable {

        val alreadyBoundLocations: Set<Vehicle.Kind.Location>

        @Parcelize
        @JvmInline
        value class ReadyToBind(
            override val alreadyBoundLocations: Set<Vehicle.Kind.Location>
        ) : State

        @Parcelize
        data class BoundToAnOtherVehicle(
            override val alreadyBoundLocations: Set<Vehicle.Kind.Location>,
            val boundVehicle: Vehicle,
            val boundVehicleLocation: Vehicle.Kind.Location
        ) : State
    }

    val stateFlow: StateFlow<State>

    fun bind(location: Vehicle.Kind.Location)
}
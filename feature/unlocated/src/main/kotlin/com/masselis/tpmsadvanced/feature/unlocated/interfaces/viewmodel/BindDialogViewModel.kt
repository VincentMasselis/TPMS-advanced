package com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface BindDialogViewModel {
    sealed interface State : Parcelable {

        val currentVehicle: Vehicle
        val alreadyBoundLocations: Set<Location>

        @Parcelize
        data class ReadyToBind(
            override val currentVehicle: Vehicle,
            override val alreadyBoundLocations: Set<Location>,
        ) : State

        @Parcelize
        data class BoundToAnOtherVehicle(
            override val currentVehicle: Vehicle,
            override val alreadyBoundLocations: Set<Location>,
            val boundVehicle: Vehicle,
            val boundVehicleLocation: Location,
        ) : State
    }

    val stateFlow: StateFlow<State>

    fun bind(location: Location)
}

package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface CurrentVehicleDropdownViewModel {

    sealed class State : Parcelable {
        @Parcelize
        data object Loading : State()

        @Parcelize
        data class Vehicles(
            val current: Vehicle,
            val list: List<Vehicle>,
        ) : State()
    }

    val stateFlow: StateFlow<State>

    fun setCurrent(vehicle: Vehicle)

    fun insert(carName: String, kind: Vehicle.Kind)
}

package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface BindSensorButtonViewModel {
    sealed class State : Parcelable {
        @Parcelize
        data object Empty : State()

        sealed class RequestBond : State() {
            abstract val foundSensor: Sensor

            @Parcelize
            data class NewBinding(override val foundSensor: Sensor) : RequestBond()

            @Parcelize
            data class AlreadyBound(
                override val foundSensor: Sensor,
                val currentVehicle: Vehicle,
                val targetVehicle: Vehicle
            ) : RequestBond()
        }
    }

    val stateFlow: StateFlow<State>

    fun bind()
}

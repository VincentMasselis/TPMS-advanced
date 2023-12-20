package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface ListSensorViewModel {
    sealed interface State : Parcelable {

        @Parcelize
        data object UnplugEverySensor : State

        @Parcelize
        data class Searching(
            val currentVehicleName: String,
            val currentVehicleKind: Vehicle.Kind,
            val boundSensorToCurrentVehicle: List<Pair<Sensor, Tyre.Unlocated?>>,
            val unboundTyres: List<Tyre.Unlocated>,
            val boundTyresToOtherVehicle: List<Triple<Vehicle, Sensor, Tyre.Unlocated>>,
            val pressureUnit: PressureUnit,
            val temperatureUnit: TemperatureUnit,
            val allWheelsBound: Boolean,
            val showPlaceholder: Boolean,
        ) : State

        @Parcelize
        data object Issue : State
    }

    val stateFlow: StateFlow<State>

    fun acknowledgeSensorUnplugged()

    fun onSensorBound()
}
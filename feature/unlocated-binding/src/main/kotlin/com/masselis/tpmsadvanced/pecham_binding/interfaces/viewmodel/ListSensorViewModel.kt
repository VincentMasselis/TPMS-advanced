package com.masselis.tpmsadvanced.pecham_binding.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.pecham_binding.usecase.ListTyreUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface ListSensorViewModel {
    sealed interface State : Parcelable {

        @Parcelize
        data object Empty : State

        @Parcelize
        data class Tyres(
            val vehicleKind: Vehicle.Kind,
            val listReadyToBind: List<ListTyreUseCase.Available.ReadyToBind>,
            val listBoundTyre: List<ListTyreUseCase.Available.Bound>,
            val pressureUnit: PressureUnit,
            val temperatureUnit: TemperatureUnit,
        ) : State

        @Parcelize
        data object Issue : State
    }

    val stateFlow: StateFlow<State>
}
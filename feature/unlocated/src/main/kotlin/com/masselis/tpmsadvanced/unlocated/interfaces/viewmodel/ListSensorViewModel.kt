package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.unlocated.usecase.ListTyreUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface ListSensorViewModel {
    sealed interface State : Parcelable {

        @Parcelize
        data object UnplugEverySensor : State

        sealed interface Searching : State {
            val allWheelsBound: Boolean
        }

        @Parcelize
        data class SearchingNoResult(override val allWheelsBound: Boolean) : Searching

        @Parcelize
        data class SearchingFoundTyre(
            val listReadyToBind: List<ListTyreUseCase.Available.ReadyToBind>,
            val listAlreadyBoundTyre: List<ListTyreUseCase.Available.AlreadyBound>,
            val pressureUnit: PressureUnit,
            val temperatureUnit: TemperatureUnit,
            override val allWheelsBound: Boolean,
        ) : Searching

        @Parcelize
        data object Issue : State
    }

    val stateFlow: StateFlow<State>

    fun acknowledgeSensorUnplugged()

    fun onSensorBound()
}
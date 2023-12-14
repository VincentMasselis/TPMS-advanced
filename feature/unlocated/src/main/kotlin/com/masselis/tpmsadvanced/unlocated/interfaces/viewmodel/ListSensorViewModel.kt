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
        data object PlugSensor : State

        sealed interface Searching : State

        @Parcelize
        data object SearchingNoResult : Searching

        @Parcelize
        data class SearchingFoundTyre(
            val listReadyToBind: List<ListTyreUseCase.Available.ReadyToBind>,
            val listAlreadyBoundTyre: List<ListTyreUseCase.Available.AlreadyBound>,
            val pressureUnit: PressureUnit,
            val temperatureUnit: TemperatureUnit,
        ) : Searching

        @Parcelize
        data object Issue : State
    }

    val stateFlow: StateFlow<State>

    fun acknowledgePlugSensor()

    fun onSensorBound()
}
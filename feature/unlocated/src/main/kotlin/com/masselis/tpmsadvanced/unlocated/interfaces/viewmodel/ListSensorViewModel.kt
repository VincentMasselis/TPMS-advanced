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


        @Parcelize
        data class Searching(
            val bindListState: BindListState,
            val listAlreadyBoundTyre: List<ListTyreUseCase.Available.AlreadyBound>,
            val pressureUnit: PressureUnit,
            val temperatureUnit: TemperatureUnit,
            val allWheelsBound: Boolean,
        ) : State {
            sealed interface BindListState : Parcelable

            @Parcelize
            data object ShowPlaceholder : BindListState

            @JvmInline
            @Parcelize
            value class ShowList(
                val listReadyToBind: List<ListTyreUseCase.Available.ReadyToBind>
            ) : BindListState

        }

        @Parcelize
        data object Issue : State
    }

    val stateFlow: StateFlow<State>

    fun acknowledgeSensorUnplugged()

    fun onSensorBound()
}
package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

internal interface TyreStatsViewModel {

    sealed class State : Parcelable {
        // Shows "-:-" texts
        @Parcelize
        data object NotDetected : State()

        // Shows the read values from the tyre
        @Parcelize
        data class Normal(
            val pressure: Pressure,
            val pressureUnit: PressureUnit,
            val temperature: Temperature,
            val temperatureUnit: TemperatureUnit,
        ) : State()

        // Show the read values from the tyre in red
        @Parcelize
        data class Alerting(
            val pressure: Pressure,
            val pressureUnit: PressureUnit,
            val temperature: Temperature,
            val temperatureUnit: TemperatureUnit,
        ) : State()
    }

    val stateFlow: StateFlow<State>
}

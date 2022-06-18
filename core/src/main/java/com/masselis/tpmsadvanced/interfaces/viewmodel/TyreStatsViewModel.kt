package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature

class TyreStatsViewModel : ViewModel() {
    sealed class State {
        // Shows "-:-" texts
        object NotDetected : State()

        // Shows the last known value in grey
        data class Obsolete(val pressure: Pressure, val temperature: Temperature) : State()

        // Shows the read values from the tyre
        data class Normal(val pressure: Pressure, val temperature: Temperature) : State()

        // Show the read values from the tyre in red
        data class Alerting(val pressure: Pressure, val temperature: Temperature) : State()
    }
}
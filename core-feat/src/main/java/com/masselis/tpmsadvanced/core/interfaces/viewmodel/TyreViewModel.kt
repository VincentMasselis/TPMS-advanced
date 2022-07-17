package com.masselis.tpmsadvanced.core.interfaces.viewmodel

import android.os.Parcelable
import com.masselis.tpmsadvanced.core.model.Fraction
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

interface TyreViewModel {

    sealed class State : Parcelable {
        // Shows an outlined tyre icon
        @Parcelize
        object NotDetected : State()

        // Shows a blue/green/red tyre
        sealed class Normal : State() {
            abstract val fraction: Fraction

            @Parcelize
            data class BlueToGreen(override val fraction: Fraction) : Normal()

            @Parcelize
            data class GreenToRed(override val fraction: Fraction) : Normal()
        }

        // Shows a blinking red tyre, could be an alert for the temperature or the pressure
        @Parcelize
        object Alerting : State()
    }

    val stateFlow : StateFlow<State>
}
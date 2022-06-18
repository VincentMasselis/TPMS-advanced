package com.masselis.tpmsadvanced.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.model.Fraction
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.TyreAtmosphereUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class TyreViewModel @AssistedInject constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): TyreViewModel
    }

    sealed class State : Parcelable {
        // Shows an outlined tyre icon
        @Parcelize
        object NotDetected : State()

        // Shows a grey tyre
        @Parcelize
        object Obsolete : State()

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

    private val state = savedStateHandle
        .getLiveData<State>("STATE", State.NotDetected)
        .asMutableStateFlow()
    val stateFlow = state.asStateFlow()

    init {
        viewModelScope.launch {

        }
    }
}
package com.masselis.tpmsadvanced.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.AtmosphereRangeUseCase
import com.masselis.tpmsadvanced.usecase.TyreAtmosphereUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize

class TyreStatsViewModel @AssistedInject constructor(
    tyreAtmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: AtmosphereRangeUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): TyreStatsViewModel
    }

    sealed class State : Parcelable {
        // Shows "-:-" texts
        @Parcelize
        object NotDetected : State()

        // Shows the read values from the tyre
        @Parcelize
        data class Normal(val pressure: Pressure, val temperature: Temperature) : State()

        // Show the read values from the tyre in red
        @Parcelize
        data class Alerting(val pressure: Pressure, val temperature: Temperature) : State()
    }

    private val state = savedStateHandle
        .getLiveData<State>("STATE", State.NotDetected)
        .asMutableStateFlow()
    val stateFlow = state.asStateFlow()

    init {
        combine(tyreAtmosphereUseCase.listen(), rangeUseCase.highTempFlow) { a, b -> a to b }
            .map { (atmosphere, highTemp) ->
                if (atmosphere.pressure.hasPressure().not())
                    State.Alerting(atmosphere.pressure, atmosphere.temperature)
                else
                    when (atmosphere.temperature.celsius) {
                        in Float.MIN_VALUE..highTemp.celsius ->
                            State.Normal(atmosphere.pressure, atmosphere.temperature)
                        in highTemp.celsius..Float.MAX_VALUE ->
                            State.Alerting(atmosphere.pressure, atmosphere.temperature)
                        else ->
                            throw IllegalArgumentException()
                    }
            }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    companion object
}
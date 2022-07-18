package com.masselis.tpmsadvanced.core.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.model.Pressure
import com.masselis.tpmsadvanced.core.model.Temperature
import com.masselis.tpmsadvanced.core.model.TyreAtmosphere
import com.masselis.tpmsadvanced.core.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.core.usecase.AtmosphereRangeUseCase
import com.masselis.tpmsadvanced.core.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.core.usecase.UnitUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize

class TyreStatsViewModel @AssistedInject constructor(
    tyreAtmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: AtmosphereRangeUseCase,
    unitUseCase: UnitUseCase,
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
        data class Normal(
            val pressure: Pressure,
            val pressureUnit: Pressure.Unit,
            val temperature: Temperature,
            val temperatureUnit: Temperature.Unit,
        ) : State()

        // Show the read values from the tyre in red
        @Parcelize
        data class Alerting(
            val pressure: Pressure,
            val pressureUnit: Pressure.Unit,
            val temperature: Temperature,
            val temperatureUnit: Temperature.Unit,
        ) : State()
    }

    private val state = savedStateHandle
        .getLiveData<State>("STATE", State.NotDetected)
        .asMutableStateFlow()
    val stateFlow = state.asStateFlow()

    init {
        combine(
            tyreAtmosphereUseCase.listen(),
            rangeUseCase.highTempFlow,
            unitUseCase.pressure.asStateFlow(),
            unitUseCase.temperature.asStateFlow(),
        ) { a, b, c, d -> Data(a, b, c, d) }
            .map { (atmosphere, highTemp, pressureUnit, temperatureUnit) ->
                if (atmosphere.pressure.hasPressure().not()) State.Alerting(
                    atmosphere.pressure,
                    pressureUnit,
                    atmosphere.temperature,
                    temperatureUnit
                ) else
                    when (atmosphere.temperature.celsius) {
                        in Float.MIN_VALUE..highTemp.celsius -> State.Normal(
                            atmosphere.pressure,
                            pressureUnit,
                            atmosphere.temperature,
                            temperatureUnit
                        )
                        in highTemp.celsius..Float.MAX_VALUE -> State.Alerting(
                            atmosphere.pressure,
                            pressureUnit,
                            atmosphere.temperature,
                            temperatureUnit
                        )
                        else ->
                            throw IllegalArgumentException()
                    }
            }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    private data class Data(
        val atmosphere: TyreAtmosphere,
        val highTemp: Temperature,
        val pressureUnit: Pressure.Unit,
        val temperature: Temperature.Unit
    )
}
package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.interfaces.AtmosphereRangePreferences
import com.masselis.tpmsadvanced.core.feature.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.record.model.TyreAtmosphere
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.parcelize.Parcelize

internal class TyreStatsViewModel @AssistedInject constructor(
    tyreAtmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: AtmosphereRangePreferences,
    unitPreferences: UnitPreferences,
    @Assisted savedStateHandle: SavedStateHandle
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

    private val state = savedStateHandle
        .getLiveData<State>("STATE", State.NotDetected)
        .asMutableStateFlow()
    val stateFlow = state.asStateFlow()

    init {
        combine(
            tyreAtmosphereUseCase.listen()
                .onEach { savedStateHandle[LAST_KNOWN] = it }
                .onStart { savedStateHandle.get<TyreAtmosphere>(LAST_KNOWN)?.also { emit(it) } },
            rangeUseCase.highTempFlow,
            unitPreferences.pressure.asStateFlow(),
            unitPreferences.temperature.asStateFlow(),
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
                            @Suppress("ThrowingExceptionsWithoutMessageOrCause")
                            throw IllegalArgumentException()
                    }
            }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    private data class Data(
        val atmosphere: TyreAtmosphere,
        val highTemp: Temperature,
        val pressureUnit: PressureUnit,
        val temperature: TemperatureUnit
    )

    companion object {
        private const val LAST_KNOWN = "LAST_KNOWN"
    }
}

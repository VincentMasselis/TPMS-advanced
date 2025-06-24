package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreStatsViewModel.State
import com.masselis.tpmsadvanced.feature.main.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.Float.Companion.NEGATIVE_INFINITY
import kotlin.Float.Companion.POSITIVE_INFINITY

internal class TyreStatsViewModelImpl @AssistedInject constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: VehicleRangesUseCase,
    unitPreferences: UnitPreferences,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel(), TyreStatsViewModel {

    @AssistedFactory
    interface Factory : (SavedStateHandle) -> TyreStatsViewModelImpl

    private val mutableStateFlow = savedStateHandle.getMutableStateFlow<State>(
        "STATE",
        State.NotDetected
    )
    override val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(
            atmosphereUseCase.listen()
                .onEach { savedStateHandle[LAST_KNOWN] = it }
                .onStart { savedStateHandle.get<TyreAtmosphere>(LAST_KNOWN)?.also { emit(it) } },
            rangeUseCase.highTemp,
            rangeUseCase.lowPressure,
            rangeUseCase.highPressure,
            unitPreferences.pressure.asStateFlow(),
            unitPreferences.temperature.asStateFlow(),
        ) { values ->
            @Suppress("MagicNumber")
            (Data(
                values[0] as TyreAtmosphere,
                values[1] as Temperature,
                values[2] as Pressure,
                values[3] as Pressure,
                values[4] as PressureUnit,
                values[5] as TemperatureUnit
            ))
        }
            .map { (atmosphere, highTemp, lowPressure, highPressure, pressureUnit, temperatureUnit) ->
                if (atmosphere.pressure.hasPressure().not() ||
                    atmosphere.pressure !in lowPressure..highPressure
                ) State.Alerting(
                    atmosphere.pressure,
                    pressureUnit,
                    atmosphere.temperature,
                    temperatureUnit
                ) else
                    when (atmosphere.temperature.celsius) {
                        in NEGATIVE_INFINITY..highTemp.celsius -> State.Normal(
                            atmosphere.pressure,
                            pressureUnit,
                            atmosphere.temperature,
                            temperatureUnit
                        )

                        in highTemp.celsius..POSITIVE_INFINITY -> State.Alerting(
                            atmosphere.pressure,
                            pressureUnit,
                            atmosphere.temperature,
                            temperatureUnit
                        )

                        else -> error("Unreachable state")
                    }
            }
            .catch { emit(State.NotDetected) }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    private data class Data(
        val tyreAtmosphere: TyreAtmosphere,
        val highTemp: Temperature,
        val lowPressure: Pressure,
        val highPressure: Pressure,
        val pressureUnit: PressureUnit,
        val temperature: TemperatureUnit
    )

    companion object {
        private const val LAST_KNOWN = "LAST_KNOWN"
    }
}

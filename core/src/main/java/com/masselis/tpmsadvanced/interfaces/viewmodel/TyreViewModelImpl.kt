package com.masselis.tpmsadvanced.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.model.Fraction
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.TyreAtmosphere
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.AtmosphereRangeUseCase
import com.masselis.tpmsadvanced.usecase.TyreAtmosphereUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

@OptIn(ExperimentalCoroutinesApi::class)
class TyreViewModelImpl @AssistedInject constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: AtmosphereRangeUseCase,
    @Assisted obsoleteTimeoutJava: Duration,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel(), TyreViewModel {

    @AssistedFactory
    interface Factory {
        fun build(
            savedStateHandle: SavedStateHandle,
            obsoleteTimeout: Duration = 2.minutes.toJavaDuration()
        ): TyreViewModelImpl
    }

    private val state = savedStateHandle
        .getLiveData<State>("STATE", State.NotDetected)
        .asMutableStateFlow()
    override val stateFlow = state.asStateFlow()

    private val obsoleteTimeout = obsoleteTimeoutJava.toKotlinDuration()

    init {
        combine(
            atmosphereUseCase.listen(),
            rangeUseCase.highTempFlow,
            rangeUseCase.normalTempFlow,
            rangeUseCase.lowTempFlow,
            rangeUseCase.lowPressureFlow
        ) { tyreAtmosphere, highTemp, normalTemp, lowTemp, lowPressure ->
            Data(tyreAtmosphere, highTemp, normalTemp, lowTemp, lowPressure)
        }.flatMapLatest { (atmosphere, highTemp, normalTemp, lowTemp, lowPressure) ->
            flow {
                emit(
                    if (atmosphere.pressure.hasPressure().not()
                        || atmosphere.pressure < lowPressure
                    ) State.Alerting
                    else
                        when (atmosphere.temperature) {
                            in Temperature(Float.MIN_VALUE)..lowTemp ->
                                State.Normal.BlueToGreen(Fraction(0f))
                            in lowTemp..normalTemp ->
                                State.Normal.BlueToGreen(
                                    Fraction(
                                        atmosphere.temperature.celsius
                                            .minus(lowTemp.celsius)
                                            .div(normalTemp.celsius - lowTemp.celsius)
                                    )
                                )
                            in normalTemp..highTemp ->
                                State.Normal.GreenToRed(
                                    Fraction(
                                        atmosphere.temperature.celsius
                                            .minus(normalTemp.celsius)
                                            .div(highTemp.celsius - normalTemp.celsius)
                                    )
                                )
                            in highTemp..Temperature(Float.MAX_VALUE) ->
                                State.Alerting
                            else ->
                                throw IllegalArgumentException()
                        }
                )
                atmosphere.timestamp
                    .plus(obsoleteTimeout.toDouble(DurationUnit.SECONDS))
                    .let { it - System.currentTimeMillis().div(1000.0) }
                    .seconds
                    .also { delay(it) }
                emit(State.NotDetected)
            }
        }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    private data class Data(
        val tyreAtmosphere: TyreAtmosphere,
        val highTemp: Temperature,
        val normalTemp: Temperature,
        val lowTemp: Temperature,
        val lowPressure: Pressure
    )
}
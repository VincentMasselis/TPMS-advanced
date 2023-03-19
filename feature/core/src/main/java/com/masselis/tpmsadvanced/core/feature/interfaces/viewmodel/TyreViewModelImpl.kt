package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModel.State
import com.masselis.tpmsadvanced.core.feature.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.core.ui.asMutableStateFlow
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.record.model.TyreAtmosphere
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.time.Duration
import kotlin.Float.Companion.NEGATIVE_INFINITY
import kotlin.Float.Companion.POSITIVE_INFINITY
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

@OptIn(ExperimentalCoroutinesApi::class)
internal class TyreViewModelImpl @AssistedInject constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: VehicleRangesUseCase,
    @Assisted obsoleteTimeoutJava: Duration,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel(), TyreViewModel {

    @AssistedFactory
    interface Factory {
        fun build(
            savedStateHandle: SavedStateHandle,
            obsoleteTimeout: Duration = 5.minutes.toJavaDuration()
        ): TyreViewModelImpl
    }

    private val state = savedStateHandle
        .getLiveData<State>("STATE", State.NotDetected)
        .asMutableStateFlow()
    override val stateFlow = state.asStateFlow()

    private val obsoleteTimeout = obsoleteTimeoutJava.toKotlinDuration()

    init {
        combine(
            atmosphereUseCase.listen()
                .onEach { savedStateHandle[LAST_KNOWN] = it }
                .onStart { savedStateHandle.get<TyreAtmosphere>(LAST_KNOWN)?.also { emit(it) } },
            rangeUseCase.highTemp,
            rangeUseCase.normalTemp,
            rangeUseCase.lowTemp,
            rangeUseCase.lowPressure,
            rangeUseCase.highPressure,
        ) { values ->
            @Suppress("MagicNumber")
            Data(
                values[0] as TyreAtmosphere,
                values[1] as Temperature,
                values[2] as Temperature,
                values[3] as Temperature,
                values[4] as Pressure,
                values[5] as Pressure
            )
        }.flatMapLatest { (atmosphere, highTemp, normalTemp, lowTemp, lowPressure, highPressure) ->
            flow {
                emit(
                    if (atmosphere.pressure.hasPressure().not() ||
                        atmosphere.pressure !in lowPressure..highPressure
                    )
                        State.Alerting
                    else
                        when (atmosphere.temperature) {
                            in Temperature(NEGATIVE_INFINITY)..lowTemp ->
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
                            in highTemp..Temperature(POSITIVE_INFINITY) ->
                                State.Alerting
                            else ->
                                @Suppress("ThrowingExceptionsWithoutMessageOrCause")
                                throw IllegalArgumentException()
                        }
                )
                atmosphere.timestamp
                    .plus(obsoleteTimeout.toDouble(DurationUnit.SECONDS))
                    .let { it - now() }
                    .seconds
                    .also { delay(it) }
                emit(State.NotDetected)
            }
        }.onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    private data class Data(
        val tyreAtmosphere: TyreAtmosphere,
        val highTemp: Temperature,
        val normalTemp: Temperature,
        val lowTemp: Temperature,
        val lowPressure: Pressure,
        val highPressure: Pressure
    )

    companion object {
        private const val LAST_KNOWN = "LAST_KNOWN"
    }
}

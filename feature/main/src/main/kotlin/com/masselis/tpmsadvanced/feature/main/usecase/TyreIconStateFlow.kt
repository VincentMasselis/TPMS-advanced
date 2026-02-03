package com.masselis.tpmsadvanced.feature.main.usecase

import android.os.Parcelable
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Suppress("OPT_IN_TO_INHERITANCE")
@OptIn(ExperimentalCoroutinesApi::class)
public class TyreIconStateFlow internal constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: VehicleRangesUseCase,
    scope: CoroutineScope,
    stateFlow: StateFlow<State> = combine(
        atmosphereUseCase.listen(),
        rangeUseCase.highTemp,
        rangeUseCase.normalTemp,
        rangeUseCase.lowTemp,
        rangeUseCase.lowPressure,
        rangeUseCase.highPressure,
    ) { values ->
        @Suppress("MagicNumber")
        (Data(
            values[0] as TyreAtmosphere,
            values[1] as Temperature,
            values[2] as Temperature,
            values[3] as Temperature,
            values[4] as Pressure,
            values[5] as Pressure
        ))
    }
        .transformLatest { (atmosphere, highTemp, normalTemp, lowTemp, lowPressure, highPressure) ->
            emit(
                if (atmosphere.pressure.hasPressure().not() ||
                    atmosphere.pressure !in lowPressure..highPressure
                )
                    State.Alerting
                else
                    when (atmosphere.temperature) {
                        in Temperature(Float.NEGATIVE_INFINITY)..lowTemp ->
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

                        in highTemp..Temperature(Float.POSITIVE_INFINITY) ->
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
        .catch {
            Firebase.crashlytics.recordException(it)
            emit(State.DetectionIssue)
        }
        .stateIn(scope, WhileSubscribed(), State.NotDetected),
) : StateFlow<State> by stateFlow {

    private data class Data(
        val tyreAtmosphere: TyreAtmosphere,
        val highTemp: Temperature,
        val normalTemp: Temperature,
        val lowTemp: Temperature,
        val lowPressure: Pressure,
        val highPressure: Pressure
    )

    public sealed interface State : Parcelable {
        // Shows an outlined tyre icon
        @Parcelize
        public data object NotDetected : State

        // Shows a blue/green/red tyre
        public sealed interface Normal : State {
            public val fraction: Fraction

            @Parcelize
            public data class BlueToGreen(override val fraction: Fraction) : Normal

            @Parcelize
            public data class GreenToRed(override val fraction: Fraction) : Normal
        }

        // Shows a blinking red tyre, could be an alert for the temperature or the pressure
        @Parcelize
        public data object Alerting : State

        @Parcelize
        public data object DetectionIssue : State
    }

    private companion object {
        private val obsoleteTimeout = 5.minutes
    }
}

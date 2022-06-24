package com.masselis.tpmsadvanced.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.model.Fraction
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.TyreAtmosphereUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize
import java.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

@OptIn(ExperimentalCoroutinesApi::class)
class TyreViewModel @AssistedInject constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    @Assisted obsoleteTimeout: Duration,
    @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(
            savedStateHandle: SavedStateHandle,
            obsoleteTimeout: Duration = 2.minutes.toJavaDuration()
        ): TyreViewModel
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
        atmosphereUseCase.listen()
            .flatMapLatest { atmosphere ->
                flow {
                    emit(
                        if (atmosphere.pressure.hasPressure().not())
                            State.Alerting
                        else
                            when (atmosphere.temperature.celsius) {
                                in Float.MIN_VALUE..LOW_TEMP.celsius ->
                                    State.Normal.BlueToGreen(Fraction(0f))
                                in LOW_TEMP.celsius..NORMAL_TEMP.celsius ->
                                    State.Normal.BlueToGreen(
                                        Fraction(
                                            atmosphere.temperature.celsius
                                                .minus(LOW_TEMP.celsius)
                                                .div(NORMAL_TEMP.celsius - LOW_TEMP.celsius)
                                        )
                                    )
                                in NORMAL_TEMP.celsius..HIGH_TEMP.celsius ->
                                    State.Normal.GreenToRed(
                                        Fraction(
                                            atmosphere.temperature.celsius
                                                .minus(NORMAL_TEMP.celsius)
                                                .div(HIGH_TEMP.celsius - NORMAL_TEMP.celsius)
                                        )
                                    )
                                in HIGH_TEMP.celsius..Float.MAX_VALUE ->
                                    State.Alerting
                                else ->
                                    throw IllegalArgumentException()
                            }
                    )
                    delay(obsoleteTimeout.toKotlinDuration())
                    emit(State.Obsolete)
                }
            }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    companion object {
        private val HIGH_TEMP = Temperature(90f)
        private val NORMAL_TEMP = Temperature(45f)
        private val LOW_TEMP = Temperature(20f)
    }
}
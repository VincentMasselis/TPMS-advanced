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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize
import java.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

@OptIn(ExperimentalCoroutinesApi::class)
class TyreStatsViewModel @AssistedInject constructor(
    tyreAtmosphereUseCase: TyreAtmosphereUseCase,
    ranges: AtmosphereRangeUseCase,
    @Assisted obsoleteTimeout: Duration,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(
            savedStateHandle: SavedStateHandle,
            obsoleteTimeout: Duration = 2.minutes.toJavaDuration()
        ): TyreStatsViewModel
    }

    sealed class State : Parcelable {
        // Shows "-:-" texts
        @Parcelize
        object NotDetected : State()

        // Shows the last known value in grey
        @Parcelize
        data class Obsolete(val pressure: Pressure, val temperature: Temperature) : State()

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
        tyreAtmosphereUseCase
            .listen()
            .flatMapLatest { atmosphere ->
                flow {
                    emit(
                        if (atmosphere.pressure.hasPressure().not())
                            State.Alerting(atmosphere.pressure, atmosphere.temperature)
                        else
                            when (atmosphere.temperature.celsius) {
                                in Float.MIN_VALUE..ranges.highTemp.celsius ->
                                    State.Normal(atmosphere.pressure, atmosphere.temperature)
                                in ranges.highTemp.celsius..Float.MAX_VALUE ->
                                    State.Alerting(atmosphere.pressure, atmosphere.temperature)
                                else ->
                                    throw IllegalArgumentException()
                            }
                    )
                    delay(obsoleteTimeout.toKotlinDuration())
                    emit(State.Obsolete(atmosphere.pressure, atmosphere.temperature))
                }
            }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    companion object
}
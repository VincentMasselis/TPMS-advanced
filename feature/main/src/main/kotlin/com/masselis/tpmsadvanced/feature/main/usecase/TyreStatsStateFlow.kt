package com.masselis.tpmsadvanced.feature.main.usecase

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize

@Suppress("OPT_IN_TO_INHERITANCE")
public class TyreStatsStateFlow internal constructor(
    atmosphereUseCase: TyreAtmosphereUseCase,
    rangeUseCase: VehicleRangesUseCase,
    location: Location,
    unitPreferences: UnitPreferences,
    scope: CoroutineScope,
    stateFlow: StateFlow<State> = combine(
        atmosphereUseCase.listen(),
        rangeUseCase.highTemp,
        rangeUseCase.resolvedLowPressure(location),
        rangeUseCase.resolvedHighPressure(location),
        unitPreferences.pressure,
        unitPreferences.temperature,
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
            val isPressureAlert = atmosphere.pressure.hasPressure().not() ||
                atmosphere.pressure !in lowPressure..highPressure
            val isTemperatureAlert = atmosphere.temperature.celsius > highTemp.celsius
            if (isPressureAlert || isTemperatureAlert) State.Alerting(
                atmosphere.timestamp,
                atmosphere.sensorId,
                atmosphere.pressure,
                pressureUnit,
                atmosphere.temperature,
                temperatureUnit,
                isPressureAlert,
                isTemperatureAlert,
            ) else State.Normal(
                atmosphere.timestamp,
                atmosphere.sensorId,
                atmosphere.pressure,
                pressureUnit,
                atmosphere.temperature,
                temperatureUnit
            )
        }
        .catch { emit(State.NotDetected) }
        .stateIn(scope, WhileSubscribed(), State.NotDetected),
) : StateFlow<State> by stateFlow {

    private data class Data(
        val tyreAtmosphere: TyreAtmosphere,
        val highTemp: Temperature,
        val lowPressure: Pressure,
        val highPressure: Pressure,
        val pressureUnit: PressureUnit,
        val temperature: TemperatureUnit
    )

    public sealed class State : Parcelable {
        // Shows "-:-" texts
        @Parcelize
        public data object NotDetected : State()

        // Shows the read values from the tyre
        @Parcelize
        public data class Normal(
            public val timestamp: Double,
            public val sensorId: Int,
            public val pressure: Pressure,
            public val pressureUnit: PressureUnit,
            public val temperature: Temperature,
            public val temperatureUnit: TemperatureUnit,
        ) : State()

        // Show the read values from the tyre, with the offending item(s) in red
        @Parcelize
        public data class Alerting(
            public val timestamp: Double,
            public val sensorId: Int,
            public val pressure: Pressure,
            public val pressureUnit: PressureUnit,
            public val temperature: Temperature,
            public val temperatureUnit: TemperatureUnit,
            public val isPressureAlert: Boolean,
            public val isTemperatureAlert: Boolean,
        ) : State()
    }
}

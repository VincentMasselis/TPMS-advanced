package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.core.database.QueryOne.Companion.asOne
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
@Suppress("MaxLineLength")
@OptIn(FlowPreview::class)
public class VehicleRangesUseCase internal constructor(
    private val vehicle: Vehicle,
    private val database: VehicleDatabase,
    scope: CoroutineScope,
) {

    public val lowPressure: MutableStateFlow<Pressure> =
        MutableStateFlow(database.selectLowPressure(vehicle.uuid))
    public val highPressure: MutableStateFlow<Pressure> =
        MutableStateFlow(database.selectHighPressure(vehicle.uuid))

    private val rearLowPressure: MutableStateFlow<Pressure> = database
        .selectRearLowPressure(vehicle.uuid)
        .let(::MutableStateFlow)

    private val rearHighPressure: MutableStateFlow<Pressure> = database
        .selectRearHighPressure(vehicle.uuid)
        .let(::MutableStateFlow)

    public val rearPressures: StateFlow<RearPressures> =
        if (vehicle.kind == Vehicle.Kind.SINGLE_AXLE_TRAILER)
            MutableStateFlow(RearPressures.Unavailable)
        else database
            .selectSeparateFrontRearPressure(vehicle.uuid)
            .asOne()
            .asChillFlow()
            .map(::computeRearState)
            .stateIn(
                scope,
                SharingStarted.Eagerly,
                computeRearState(
                    database.selectSeparateFrontRearPressure(vehicle.uuid).executeAsOne()
                )
            )

    public val lowTemp: MutableStateFlow<Temperature> =
        MutableStateFlow(database.selectLowTemp(vehicle.uuid))
    public val normalTemp: MutableStateFlow<Temperature> =
        MutableStateFlow(database.selectNormalTemp(vehicle.uuid))
    public val highTemp: MutableStateFlow<Temperature> =
        MutableStateFlow(database.selectHighTemp(vehicle.uuid))

    init {
        lowPressure
            .debounce(100.milliseconds)
            .onEach { database.updateLowPressure(it, vehicle.uuid) }
            .launchIn(scope)

        highPressure
            .debounce(100.milliseconds)
            .onEach { database.updateHighPressure(it, vehicle.uuid) }
            .launchIn(scope)

        rearLowPressure
            .debounce(100.milliseconds)
            .onEach { database.updateRearLowPressure(it, vehicle.uuid) }
            .launchIn(scope)

        rearHighPressure
            .debounce(100.milliseconds)
            .onEach { database.updateRearHighPressure(it, vehicle.uuid) }
            .launchIn(scope)

        lowTemp
            .debounce(100.milliseconds)
            .onEach { database.updateLowTemp(it, vehicle.uuid) }
            .launchIn(scope)

        normalTemp
            .debounce(100.milliseconds)
            .onEach { database.updateNormalTemp(it, vehicle.uuid) }
            .launchIn(scope)

        highTemp
            .debounce(100.milliseconds)
            .onEach { database.updateHighTemp(it, vehicle.uuid) }
            .launchIn(scope)
    }

    public suspend fun rearPressure(separated: Boolean) {
        require(rearPressures.value !is RearPressures.Unavailable) { "Cannot separate rear and front on a single axle trailer" }
        withContext(IO) { database.updateSeparateFrontRearPressure(separated, vehicle.uuid) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    public fun rearPressuresIfSeparated(): Flow<Pair<Pressure, Pressure>?> = rearPressures
        .flatMapLatest {
            when (it) {
                RearPressures.Unavailable, is RearPressures.Disabled ->
                    flowOf(null)

                is RearPressures.Enabled ->
                    combine(it.lowPressure, it.highPressure) { lowPress, highPress ->
                        lowPress to highPress
                    }
            }
        }

    private fun computeRearState(
        separateFrontRearPressure: Boolean?
    ) = when (separateFrontRearPressure) {
        true -> RearPressures.Enabled(rearLowPressure, rearHighPressure)
        false -> RearPressures.Disabled(rearLowPressure, rearHighPressure)
        null -> RearPressures.Unavailable
    }

    public sealed interface RearPressures {
        /** Used when [Vehicle.Kind] is [Vehicle.Kind.SINGLE_AXLE_TRAILER] */
        public data object Unavailable : RearPressures

        public sealed interface Available : RearPressures {
            public val lowPressure: MutableStateFlow<Pressure>
            public val highPressure: MutableStateFlow<Pressure>
        }

        public data class Disabled(
            override val lowPressure: MutableStateFlow<Pressure>,
            override val highPressure: MutableStateFlow<Pressure>
        ) : Available, RearPressures

        public data class Enabled(
            override val lowPressure: MutableStateFlow<Pressure>,
            override val highPressure: MutableStateFlow<Pressure>
        ) : Available, RearPressures
    }
}

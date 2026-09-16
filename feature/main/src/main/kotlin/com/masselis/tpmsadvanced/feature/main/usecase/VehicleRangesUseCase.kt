package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
public class VehicleRangesUseCase internal constructor(
    vehicle: Vehicle,
    scope: CoroutineScope,
    database: VehicleDatabase,
) {

    public val lowPressure: MutableStateFlow<Pressure> =
        MutableStateFlow(database.selectLowPressure(vehicle.uuid))
    public val highPressure: MutableStateFlow<Pressure> =
        MutableStateFlow(database.selectHighPressure(vehicle.uuid))
    public val lowTemp: MutableStateFlow<Temperature> =
        MutableStateFlow(database.selectLowTemp(vehicle.uuid))
    public val normalTemp: MutableStateFlow<Temperature> =
        MutableStateFlow(database.selectNormalTemp(vehicle.uuid))
    public val highTemp: MutableStateFlow<Temperature> =
        MutableStateFlow(database.selectHighTemp(vehicle.uuid))
    public val rearLowPressure: MutableStateFlow<Pressure?> =
        MutableStateFlow(database.selectRearLowPressure(vehicle.uuid))
    public val rearHighPressure: MutableStateFlow<Pressure?> =
        MutableStateFlow(database.selectRearHighPressure(vehicle.uuid))

    public fun setRearOverrideEnabled(enabled: Boolean) {
        if (enabled) {
            rearLowPressure.value = lowPressure.value
            rearHighPressure.value = highPressure.value
        } else {
            rearLowPressure.value = null
            rearHighPressure.value = null
        }
    }

    public fun resolvedLowPressure(location: Location): Flow<Pressure> = location
        .toAxleOrNull()
        ?.takeIf { it.axle == REAR }
        ?.let { combine(rearLowPressure, lowPressure) { rear, front -> rear ?: front } }
        ?: lowPressure

    public fun resolvedHighPressure(location: Location): Flow<Pressure> = location
        .toAxleOrNull()
        ?.takeIf { it.axle == REAR }
        ?.let { combine(rearHighPressure, highPressure) { rear, front -> rear ?: front } }
        ?: highPressure

    init {
        lowPressure
            .debounce(100.milliseconds)
            .onEach { database.updateLowPressure(it, vehicle.uuid) }
            .launchIn(scope)

        highPressure
            .debounce(100.milliseconds)
            .onEach { database.updateHighPressure(it, vehicle.uuid) }
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

        rearLowPressure
            .debounce(100.milliseconds)
            .onEach { database.updateRearLowPressure(it, vehicle.uuid) }
            .launchIn(scope)

        rearHighPressure
            .debounce(100.milliseconds)
            .onEach { database.updateRearHighPressure(it, vehicle.uuid) }
            .launchIn(scope)
    }
}

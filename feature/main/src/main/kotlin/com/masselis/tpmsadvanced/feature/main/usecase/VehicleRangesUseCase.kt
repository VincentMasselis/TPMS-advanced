package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
public class VehicleRangesUseCase internal constructor(
    private val vehicle: Vehicle,
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
    }
}

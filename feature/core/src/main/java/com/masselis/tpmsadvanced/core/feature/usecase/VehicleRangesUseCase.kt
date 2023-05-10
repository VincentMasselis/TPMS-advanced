package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@VehicleComponent.Scope
internal class VehicleRangesUseCase @Inject constructor(
    @Named("base") private val vehicle: Vehicle,
    private val database: VehicleDatabase,
    scope: CoroutineScope
) {

    val lowPressure = MutableStateFlow(database.selectLowPressure(vehicle.uuid))
    val highPressure = MutableStateFlow(database.selectHighPressure(vehicle.uuid))
    val lowTemp = MutableStateFlow(database.selectLowTemp(vehicle.uuid))
    val normalTemp = MutableStateFlow(database.selectNormalTemp(vehicle.uuid))
    val highTemp = MutableStateFlow(database.selectHighTemp(vehicle.uuid))

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

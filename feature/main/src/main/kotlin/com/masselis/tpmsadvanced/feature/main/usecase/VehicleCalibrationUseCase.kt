package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.PressureCalibration
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

/**
 * The vehicle's pressure calibration. Turning it off keeps [offset] and [multiplier], so turning it
 * back on restores them.
 */
@OptIn(FlowPreview::class)
public class VehicleCalibrationUseCase internal constructor(
    vehicle: Vehicle,
    scope: CoroutineScope,
    database: VehicleDatabase,
) {

    public val isEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(database.selectPressureCalibration(vehicle.uuid))
    public val offset: MutableStateFlow<Pressure> =
        MutableStateFlow(database.selectPressureOffset(vehicle.uuid))
    public val multiplier: MutableStateFlow<Float> =
        MutableStateFlow(database.selectPressureMultiplier(vehicle.uuid))

    /** The calibration to apply to the read pressures, null while it's off */
    public val calibration: Flow<PressureCalibration?> =
        combine(isEnabled, offset, multiplier) { enabled, offset, multiplier ->
            PressureCalibration(offset, multiplier).takeIf { enabled }
        }

    init {
        isEnabled
            .debounce(100.milliseconds)
            .onEach { database.updatePressureCalibration(it, vehicle.uuid) }
            .launchIn(scope)

        offset
            .debounce(100.milliseconds)
            .onEach { database.updatePressureOffset(it, vehicle.uuid) }
            .launchIn(scope)

        multiplier
            .debounce(100.milliseconds)
            .onEach { database.updatePressureMultiplier(it, vehicle.uuid) }
            .launchIn(scope)
    }
}

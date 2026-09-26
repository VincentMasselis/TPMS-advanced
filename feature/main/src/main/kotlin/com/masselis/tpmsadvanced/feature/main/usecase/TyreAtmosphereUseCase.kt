package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * The tyre's atmosphere, its pressure being corrected by the vehicle's calibration so the display,
 * the alerts and the background monitoring all agree on it.
 */
public class TyreAtmosphereUseCase internal constructor(
    private val listenTyreUseCase: ListenTyreUseCase,
    private val calibrationUseCase: VehicleCalibrationUseCase,
) {
    public fun listen(): Flow<TyreAtmosphere> = listenTyreUseCase
        .listen()
        .map { record ->
            TyreAtmosphere(
                record.timestamp,
                if (record.isAlarm) 0f.kpa else record.pressure,
                record.temperature
            )
        }
        .combine(calibrationUseCase.calibration) { atmosphere, calibration ->
            calibration
                ?.let { atmosphere.copy(pressure = it.applyTo(atmosphere.pressure)) }
                ?: atmosphere
        }
}

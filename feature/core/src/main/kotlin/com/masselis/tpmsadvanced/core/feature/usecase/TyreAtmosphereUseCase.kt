package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.kpa
import com.masselis.tpmsadvanced.data.record.model.TyreAtmosphere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

public class TyreAtmosphereUseCase @Inject internal constructor(
    private val tyreUseCase: TyreUseCase
) {
    public fun listen(): Flow<TyreAtmosphere> = tyreUseCase
        .listen()
        .map { record ->
            TyreAtmosphere(
                record.timestamp,
                if (record.isAlarm) 0f.kpa else record.pressure,
                record.temperature
            )
        }
}
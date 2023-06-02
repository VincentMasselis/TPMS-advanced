package com.masselis.tpmsadvanced.feature.background.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class VehiclesToMonitorUseCase @Inject constructor(
    private val vehicleDatabase: VehicleDatabase
) {
    fun ignoredAndMonitored() = vehicleDatabase
        .selectAllFlow()
        .map { vehicles ->
            vehicles
                .groupBy { it.isBackgroundMonitor }
                .let {
                    Pair(
                        it.getValue(false),
                        it.getValue(true)
                    )
                }
        }
        .flowOn(Default)
}

package com.masselis.tpmsadvanced.feature.background.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okio.withLock
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

@FeatureBackgroundComponent.Scope
internal class VehiclesToMonitorUseCase @Inject constructor(
    private val vehicleDatabase: VehicleDatabase
) {

    private val manuals = MutableStateFlow<Set<UUID>>(sortedSetOf())
    private val lock = ReentrantLock()

    fun enableManual(vehicleUuid: UUID) = lock.withLock {
        manuals.value = (manuals.value + vehicleUuid).toSortedSet()
    }

    fun disableManual(vehicleUuid: UUID) = lock.withLock {
        manuals.value = (manuals.value - vehicleUuid).toSortedSet()
    }

    fun ignoredAndMonitored() = combine(
        vehicleDatabase
            .selectAllFlow()
            .map { vehicles ->
                vehicles
                    .groupBy { it.isBackgroundMonitor }
                    .let {
                        Pair(
                            it[false] ?: emptyList(),
                            it[true] ?: emptyList()
                        )
                    }
            }
            .flowOn(Default),
        manuals
            .map { it.map { uuid -> vehicleDatabase.selectByUuid(uuid).first() } }
    ) { (ignored, monitored), manuals ->
        ignored to (monitored + manuals).distinctBy { it.uuid }
    }
}

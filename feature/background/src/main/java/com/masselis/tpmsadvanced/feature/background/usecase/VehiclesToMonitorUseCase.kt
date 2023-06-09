package com.masselis.tpmsadvanced.feature.background.usecase

import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.core.ui.isAppVisibleFlow
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.Flow
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
public class VehiclesToMonitorUseCase @Inject internal constructor(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val vehicleDatabase: VehicleDatabase,
) {

    private val manuals = MutableStateFlow<Set<UUID>>(sortedSetOf())
    private val lock = ReentrantLock()

    internal fun enableManual(vehicleUuid: UUID) = lock.withLock {
        manuals.value = (manuals.value + vehicleUuid).toSortedSet()
    }

    internal fun disableManual(vehicleUuid: UUID) = lock.withLock {
        manuals.value = (manuals.value - vehicleUuid).toSortedSet()
    }

    public fun ignoredAndMonitored(): Flow<Pair<List<Vehicle>, List<Vehicle>>> = combine(
        combine(
            currentVehicleUseCase
                .flow
                .map { it.vehicle },
            isAppVisibleFlow
        ) { current, isVisible -> current.takeIf { isVisible } },
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
            .map { it.map { uuid -> vehicleDatabase.selectByUuid(uuid).first() } },
    ) { displayedVehicle, (automaticIgnored, automaticMonitored), manuals ->
        val monitored = (automaticMonitored + manuals)
            .distinctBy { it.uuid }
            .filter { it.uuid != displayedVehicle?.uuid } // Do not monitor the vehicle if it is already displayed
        Pair(
            with(monitored.map { it.uuid }) { automaticIgnored.filter { contains(it.uuid).not() } },
            monitored
        )
    }
}

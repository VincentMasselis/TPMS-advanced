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

    public fun expectedIgnoredAndMonitored(): Flow<Pair<List<Vehicle>, List<Vehicle>>> = combine(
        vehicleDatabase.selectAllFlow().map { vehicles ->
            vehicles
                .groupBy { it.isBackgroundMonitor }
                .let {
                    Pair(
                        it[false].orEmpty(),
                        it[true].orEmpty()
                    )
                }
        },
        manuals
            .map { it.map { uuid -> vehicleDatabase.selectByUuidFlow(uuid).first() } },
    ) { (automaticIgnored, automaticMonitored), manuals ->
        Pair(
            // Removes from automaticIgnored the devices with were added to manuals
            manuals.map { it.uuid }.run { automaticIgnored.filter { contains(it.uuid).not() } },
            // Merges automaticMonitored and manual list
            (automaticMonitored + manuals).distinctBy { it.uuid }
        )
    }.flowOn(Default)

    @Suppress("NAME_SHADOWING")
    public fun realtimeIgnoredAndMonitored(): Flow<Pair<List<Vehicle>, List<Vehicle>>> = combine(
        currentVehicleUseCase.map { it.vehicle },
        isAppVisibleFlow,
        expectedIgnoredAndMonitored(),
    ) { current, isAppVisible, (ignored, monitored) ->
        val current = current.takeIf { isAppVisible }
        Pair(
            // Adds to ignored list if the current is already displayed
            (ignored + current).filterNotNull(),
            // Do not monitor the vehicle if it is already displayed
            monitored.filter { it.uuid != current?.uuid }
        )
    }.flowOn(Default)
}

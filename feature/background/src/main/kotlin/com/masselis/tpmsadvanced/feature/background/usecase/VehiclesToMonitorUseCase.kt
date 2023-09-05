package com.masselis.tpmsadvanced.feature.background.usecase

import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.core.ui.isAppVisibleFlow
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

@OptIn(DelicateCoroutinesApi::class)
@FeatureBackgroundComponent.Scope
public class VehiclesToMonitorUseCase @Inject internal constructor(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val vehicleDatabase: VehicleDatabase,
) {

    private val manuals = MutableStateFlow<Set<UUID>>(sortedSetOf())
    private val lock = ReentrantLock()

    init {
        vehicleDatabase
            .selectUuidIsDeleting()
            .map { it.toSortedSet() }
            .distinctUntilChanged()
            .onEach { lock.withLock { manuals.value = (manuals.value - it).toSortedSet() } }
            .flowOn(Default)
            .launchIn(GlobalScope)
    }

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
            with(manuals.map { it.uuid }) { automaticIgnored.filter { contains(it.uuid).not() } },
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

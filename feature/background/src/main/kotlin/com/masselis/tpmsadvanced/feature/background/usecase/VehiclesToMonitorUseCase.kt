package com.masselis.tpmsadvanced.feature.background.usecase

import com.masselis.tpmsadvanced.core.ui.isAppVisibleFlow
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
public class VehiclesToMonitorUseCase(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val vehicleDatabase: VehicleDatabase,
) {

    private val manuals = MutableStateFlow<Set<UUID>>(sortedSetOf())
    private val lock = ReentrantLock()

    init {
        vehicleDatabase
            .selectUuidIsDeleting()
            .asFlow()
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

    public fun ignoredAndMonitored(): Flow<Pair<List<Vehicle>, List<Vehicle>>> = combine(
        vehicleDatabase
            .selectAll()
            .asFlow()
            .map { vehicles ->
                vehicles
                    .groupBy { it.isBackgroundMonitor }
                    .let {
                        Pair(
                            it[false].orEmpty(),
                            it[true].orEmpty()
                        )
                    }
            },
        manuals.flatMapLatest { manuals ->
            // By default, if `combine()` is called with an empty array, combine acts like
            // `emptyFlow()` but at least one value must be emit otherwise
            // `expectedIgnoredAndMonitored()` will never return any value. As consequence, instead
            // of a `emptyFlow()` like behavior, `flowOf(emptyArray())` is used.
            if (manuals.isEmpty()) flowOf(emptyArray())
            else combine(manuals.map { vehicleDatabase.selectByUuid(it).asFlow() }) { it }
        },
    ) { (automaticIgnored, automaticMonitored), manuals ->
        Pair(
            // Removes from automaticIgnored the devices with were added to manuals
            with(manuals.map { it.uuid }) { automaticIgnored.filter { contains(it.uuid).not() } },
            // Merges automaticMonitored and manual list
            (automaticMonitored + manuals).distinctBy { it.uuid }
        )
    }.flowOn(Default)

    @Suppress("NAME_SHADOWING")
    public fun appVisibilityIgnoredAndMonitored(): Flow<Pair<List<Vehicle>, List<Vehicle>>> =
        combine(
            currentVehicleUseCase.map { it.vehicle },
            isAppVisibleFlow,
            ignoredAndMonitored(),
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

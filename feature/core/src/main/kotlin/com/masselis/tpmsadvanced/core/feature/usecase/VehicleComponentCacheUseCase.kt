package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Should be used only by the class [VehicleComponent] and [VehicleComponent.Factory].
 */
@OptIn(InternalDaggerImplementation::class, DelicateCoroutinesApi::class)
@FeatureCoreComponent.Scope
internal class VehicleComponentCacheUseCase @Inject internal constructor(
    private val vehicleFactory: VehicleComponent.Factory,
    vehicleDatabase: VehicleDatabase,
) {
    private val cache = ConcurrentHashMap<UUID, VehicleComponent>()

    init {
        vehicleDatabase
            .selectUuidIsDeleting()
            .map { it.toSortedSet() }
            .distinctUntilChanged()
            .onEach { deletingList ->
                deletingList.forEach {
                    cache.remove(it)
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(GlobalScope)
    }

    fun find(vehicle: Vehicle): VehicleComponent = cache.computeIfAbsent(vehicle.uuid) {
        vehicleFactory.daggerOnlyBuild(vehicle)
    }
}

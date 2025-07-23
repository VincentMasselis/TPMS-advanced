package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.InternalVehicleGraph
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@OptIn(DelicateCoroutinesApi::class)
internal class VehicleGraphCacheUseCase(
    vehicleDatabase: VehicleDatabase,
    private val vehicleGraphFactory: (Vehicle) -> InternalVehicleGraph,
) : (Vehicle) -> InternalVehicleGraph {

    private val cache = ConcurrentHashMap<UUID, InternalVehicleGraph>()

    init {
        vehicleDatabase
            .selectUuidIsDeleting()
            .asFlow()
            .map { it.toSortedSet() }
            .distinctUntilChanged()
            .onEach { deletingList ->
                deletingList.forEach {
                    cache.remove(it)
                }
            }
            .launchIn(GlobalScope + Default)
    }

    override fun invoke(vehicle: Vehicle) = cache.computeIfAbsent(vehicle.uuid) {
        vehicleGraphFactory(vehicle)
    }
}

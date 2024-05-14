package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.InternalVehicleComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
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
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureCoreComponent.Scope
internal class VehicleComponentCacheUseCase @Inject internal constructor(
    vehicleDatabase: VehicleDatabase,
    @Suppress("MaxLineLength") private val vehicleComponentFactory: (@JvmSuppressWildcards Vehicle) -> @JvmSuppressWildcards InternalVehicleComponent,
) : (Vehicle) -> InternalVehicleComponent {
    private val cache = ConcurrentHashMap<UUID, InternalVehicleComponent>()

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

    override fun invoke(vehicle: Vehicle): InternalVehicleComponent =
        cache.computeIfAbsent(vehicle.uuid) {
            vehicleComponentFactory(vehicle)
        }
}

package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Should be used only by the class [VehicleComponent] and [VehicleComponent.Factory].
 */
@OptIn(InternalDaggerImplementation::class)
@FeatureCoreComponent.Scope
internal class VehicleComponentCacheUseCase @Inject internal constructor(
    private val vehicleFactory: VehicleComponent.Factory
) {
    private val cache = ConcurrentHashMap<UUID, VehicleComponent>()

    fun find(vehicle: Vehicle): VehicleComponent = cache.computeIfAbsent(vehicle.uuid) {
        vehicleFactory.daggerOnlyBuild(vehicle)
    }
}

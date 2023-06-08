package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.cancel
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

/**
 * Should be used only by the class [VehicleComponent] and [VehicleComponent.Factory].
 */
@OptIn(InternalDaggerImplementation::class)
@FeatureCoreComponent.Scope
internal class ActiveVehicleComponentsUseCase @Inject internal constructor(
    private val vehicleFactory: VehicleComponent.Factory
) {

    private val actives = mutableListOf<Pair<Int, VehicleComponent>>()
    private val lock = ReentrantLock()

    fun hold(vehicle: Vehicle): VehicleComponent = lock.withLock {
        val index = actives.indexOfFirst { (_, comp) -> comp.vehicle.uuid == vehicle.uuid }
        if (index >= 0)
            actives
                .set(index, actives[index].let { it.first + 1 to it.second })
                .second
        else
            vehicleFactory
                .daggerBuild(vehicle)
                .also { actives += (1 to it) }
    }

    fun release(vehicle: Vehicle): Unit = lock.withLock {
        val index = actives.indexOfFirst { (_, comp) -> comp.vehicle.uuid == vehicle.uuid }
        val (count, comp) = actives[index]
        when (count) {
            in 2..Int.MAX_VALUE -> actives[index] = count - 1 to comp
            1 -> actives.removeAt(index).second.scope.cancel()
            else -> error("Cannot release a component vehicle because it was already release !")
        }
    }
}
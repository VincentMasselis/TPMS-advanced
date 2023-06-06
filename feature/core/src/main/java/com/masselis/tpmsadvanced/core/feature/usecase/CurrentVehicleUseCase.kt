package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.observableStateFlow
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import java.util.UUID.randomUUID
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureCoreComponent.Scope
internal class CurrentVehicleUseCase @Inject constructor(
    private val database: VehicleDatabase,
    private val factory: VehicleComponent.Factory
) {

    private val mutableStateFlow = observableStateFlow(
        factory.build(database.currentVehicle())
    ) { old, _ ->
        old.scope.cancel()
    }

    internal val flow = mutableStateFlow.asStateFlow()

    init {
        database.currentVehicleFlow()
            .distinctUntilChanged()
            .filter { it.uuid != mutableStateFlow.value.vehicle.uuid }
            .onEach { mutableStateFlow.value = factory.build(it) }
            .launchIn(GlobalScope)
    }

    internal suspend fun setAsCurrent(uuid: UUID) {
        if(uuid == flow.value.vehicle.uuid)
            return
        database
            .selectByUuid(uuid)
            .first()
            .let { setAsCurrent(it) }
    }

    internal suspend fun setAsCurrent(vehicle: Vehicle) = database.setIsCurrent(vehicle.uuid, true)

    internal suspend fun insertAsCurrent(carName: String, kind: Vehicle.Kind) = database
        .insert(randomUUID(), kind, carName, true)
}

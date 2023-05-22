package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@VehicleComponent.Scope
internal class VehicleStateFlowUseCase @Inject constructor(
    @Named("base") vehicle: Vehicle,
    database: VehicleDatabase,
    scope: CoroutineScope,
) : StateFlow<Vehicle> by database
    .selectByUuid(vehicle.uuid)
    .stateIn(scope, SharingStarted.Eagerly, vehicle)

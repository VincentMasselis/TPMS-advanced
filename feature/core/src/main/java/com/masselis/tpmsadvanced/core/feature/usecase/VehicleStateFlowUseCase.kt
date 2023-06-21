package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@VehicleComponent.Scope
internal class VehicleStateFlowUseCase @Inject constructor(
    @Named("base") vehicle: Vehicle,
    scope: CoroutineScope,
    database: VehicleDatabase,
) : StateFlow<Vehicle> by database
    .selectByUuidFlow(vehicle.uuid)
    .stateIn(scope, WhileSubscribed(), vehicle)

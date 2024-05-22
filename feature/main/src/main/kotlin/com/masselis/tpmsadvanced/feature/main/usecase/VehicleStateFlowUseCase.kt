package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Named

@VehicleComponent.Scope
internal class VehicleStateFlowUseCase @Inject constructor(
    @Named("base") vehicle: Vehicle,
    database: VehicleDatabase,
    scope: CoroutineScope,
) : StateFlow<Vehicle> by database.selectByUuid(vehicle.uuid).asStateFlow(scope + IO, Eagerly)

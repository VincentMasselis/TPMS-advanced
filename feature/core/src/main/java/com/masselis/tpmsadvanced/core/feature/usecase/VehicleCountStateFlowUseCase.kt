package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@VehicleComponent.Scope
internal class VehicleCountStateFlowUseCase @Inject constructor(
    vehicleDatabase: VehicleDatabase,
    scope: CoroutineScope
) : StateFlow<Long> by vehicleDatabase
    .countFlow()
    .stateIn(scope, SharingStarted.Eagerly, vehicleDatabase.count())

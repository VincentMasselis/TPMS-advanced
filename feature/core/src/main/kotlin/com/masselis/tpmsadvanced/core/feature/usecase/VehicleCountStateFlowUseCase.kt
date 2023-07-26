package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureCoreComponent.Scope
internal class VehicleCountStateFlowUseCase @Inject constructor(
    vehicleDatabase: VehicleDatabase,
) : StateFlow<Long> by vehicleDatabase
    .countFlow()
    .stateIn(GlobalScope, Eagerly, vehicleDatabase.count())

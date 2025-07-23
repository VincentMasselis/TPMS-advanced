package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Inject
internal class VehicleStateFlowUseCase(
    @Named("base") vehicle: Vehicle,
    database: VehicleDatabase,
    scope: CoroutineScope,
) : StateFlow<Vehicle> by database.selectByUuid(vehicle.uuid).asStateFlow(scope + IO, Eagerly)

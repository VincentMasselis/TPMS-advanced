package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(DelicateCoroutinesApi::class, ExperimentalForInheritanceCoroutinesApi::class)
internal class VehicleCountStateFlowUseCase(
    vehicleDatabase: VehicleDatabase,
) : StateFlow<Long> by vehicleDatabase.count().asStateFlow(GlobalScope + IO, Eagerly)

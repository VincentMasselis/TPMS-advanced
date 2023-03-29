package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

@VehicleComponent.Scope
internal class VehicleUseCase @Inject constructor(
    @Named("base") private val vehicle: Vehicle,
    private val database: VehicleDatabase,
    scope: CoroutineScope,
) {

    private val mutableStateFlow = MutableStateFlow(vehicle)

    init {
        database
            .selectByUuid(vehicle.uuid)
            .onEach { mutableStateFlow.value = it }
            .launchIn(scope)
    }

    fun vehicleFlow() = mutableStateFlow.asStateFlow()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun delete() {
        database.setIsCurrent(database.selectAll().first { it.uuid != vehicle.uuid }.uuid, true)
        GlobalScope.launch {
            delay(1.seconds)
            database.delete(vehicle.uuid)
        }
    }
}

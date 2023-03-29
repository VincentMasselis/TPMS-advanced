package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@VehicleComponent.Scope
internal class VehicleStateFlowUseCase private constructor(
    database: VehicleDatabase,
    scope: CoroutineScope,
    private val mutableStateFlow: MutableStateFlow<Vehicle>,
) : StateFlow<Vehicle> by mutableStateFlow.asStateFlow() {

    @Inject
    constructor(
        @Named("base") vehicle: Vehicle,
        database: VehicleDatabase,
        scope: CoroutineScope,
    ) : this(database, scope, MutableStateFlow(vehicle))


    init {
        database
            .selectByUuid(mutableStateFlow.value.uuid)
            .onEach { mutableStateFlow.value = it }
            .launchIn(scope)
    }
}

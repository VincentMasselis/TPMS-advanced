package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleGraph
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import java.util.UUID.randomUUID

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(DelicateCoroutinesApi::class, ExperimentalForInheritanceCoroutinesApi::class)
public class CurrentVehicleUseCase private constructor(
    private val database: VehicleDatabase,
    private val mutableStateFlow: MutableStateFlow<VehicleGraph>
) : StateFlow<VehicleGraph> by mutableStateFlow.asStateFlow() {

    internal constructor(
        database: VehicleDatabase,
    ) : this(
        database,
        database.currentVehicle()
            .let { query ->
                val mutableStateFlow = MutableStateFlow(VehicleGraph(query.execute()))
                query
                    .asChillFlow()
                    .filter { it.uuid != mutableStateFlow.value.vehicle.uuid }
                    .map(VehicleGraph)
                    .onEach { mutableStateFlow.value = it }
                    .launchIn(GlobalScope)
                mutableStateFlow
            }
    )

    public suspend fun setAsCurrent(uuid: UUID): Unit =
        database.setIsCurrent(uuid, true)

    public suspend fun setAsCurrent(vehicle: Vehicle): Unit =
        database.setIsCurrent(vehicle.uuid, true)

    internal suspend fun insertAsCurrent(carName: String, kind: Vehicle.Kind) = database
        .insert(randomUUID(), kind, carName, true)
}

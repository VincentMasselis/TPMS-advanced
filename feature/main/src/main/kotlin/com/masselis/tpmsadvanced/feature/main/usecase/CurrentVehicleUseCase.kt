package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
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
import javax.inject.Inject

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(DelicateCoroutinesApi::class, ExperimentalForInheritanceCoroutinesApi::class)
@FeatureCoreComponent.Scope
public class CurrentVehicleUseCase private constructor(
    private val database: VehicleDatabase,
    private val mutableStateFlow: MutableStateFlow<VehicleComponent>
) : StateFlow<VehicleComponent> by mutableStateFlow.asStateFlow() {

    @Inject
    internal constructor(
        database: VehicleDatabase,
    ) : this(
        database,
        database.currentVehicle()
            .let { query ->
                val mutableStateFlow = MutableStateFlow(VehicleComponent(query.execute()))
                query
                    .asChillFlow()
                    .filter { it.uuid != mutableStateFlow.value.vehicle.uuid }
                    .map(VehicleComponent)
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

package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.VehicleStateFlowUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

@Module
internal object VehicleModule {
    @Provides
    fun carFlow(vehicleStateFlowUseCase: VehicleStateFlowUseCase): StateFlow<Vehicle> =
        vehicleStateFlowUseCase

    @Provides
    @VehicleComponent.Scope
    fun scope() = CoroutineScope(SupervisorJob())
}

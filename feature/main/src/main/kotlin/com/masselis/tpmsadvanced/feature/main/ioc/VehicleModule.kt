package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleStateFlowUseCase
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

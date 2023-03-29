package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.VehicleStateFlowUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

@Module
internal object VehicleModule {
    @VehicleComponent.Scope
    @Provides
    fun scope(): CoroutineScope = CoroutineScope(Dispatchers.Unconfined)

    @Provides
    fun carFlow(vehicleStateFlowUseCase: VehicleStateFlowUseCase): StateFlow<Vehicle> =
        vehicleStateFlowUseCase
}

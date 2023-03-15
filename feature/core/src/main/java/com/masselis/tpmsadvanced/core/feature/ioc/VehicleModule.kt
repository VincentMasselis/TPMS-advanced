package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.VehicleUseCase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module
internal object VehicleModule {
    @VehicleComponent.Scope
    @Provides
    fun scope(): CoroutineScope = CoroutineScope(Dispatchers.Unconfined)

    @Provides
    fun carFlow(vehicleUseCase: VehicleUseCase) = vehicleUseCase.vehicleFlow()
}

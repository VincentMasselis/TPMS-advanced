package com.masselis.tpmsadvanced.feature.background.ioc

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus
import javax.inject.Named

@Module
internal object BackgroundVehicleModule {

    @Provides
    @Named("background_vehicle_component")
    @BackgroundVehicleComponent.Scope
    fun scope(@Named("vehicle_component") scope: CoroutineScope): CoroutineScope =
        scope + SupervisorJob(parent = scope.coroutineContext[Job])

    @Provides
    @Named("background_vehicle_component")
    fun release(
        @Named("background_vehicle_component") scope: CoroutineScope,
        @Named("vehicle_component") vehicleComponentRelease: () -> Unit
    ): () -> Unit = {
        scope.cancel()
        vehicleComponentRelease()
    }
}

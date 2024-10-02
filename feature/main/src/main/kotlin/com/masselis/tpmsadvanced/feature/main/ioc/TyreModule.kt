package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.usecase.ListenBoundTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreSmartDutyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreWithDatabaseUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SensorBindingUseCase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named

@Module
internal object TyreModule {
    @Provides
    @TyreComponent.Scope
    fun listenTyreWithDatabaseUseCase(
        @Named("base") vehicle: Vehicle,
        location: Location,
        tyreDatabase: TyreDatabase,
        listenTyreUseCase: ListenTyreSmartDutyUseCase,
        scope: CoroutineScope,
    ) = ListenTyreWithDatabaseUseCase(vehicle, location, tyreDatabase, listenTyreUseCase, scope)

    @Provides
    fun listenBoundTyreUseCase(
        listenTyreUseCase: ListenTyreWithDatabaseUseCase,
        sensorBindingUseCase: SensorBindingUseCase,
    ) = ListenBoundTyreUseCase(listenTyreUseCase, sensorBindingUseCase)

    @Provides
    fun listenTyreUseCase(uc: ListenBoundTyreUseCase): ListenTyreUseCase = uc
}

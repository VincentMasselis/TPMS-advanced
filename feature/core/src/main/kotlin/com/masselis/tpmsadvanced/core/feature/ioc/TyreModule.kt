package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.ListenBoundTyreUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.ListenTyreUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.ListenTyreSmartDutyUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.ListenTyreWithDatabaseUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.LocatedTyreScannerUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.SensorBindingUseCase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
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

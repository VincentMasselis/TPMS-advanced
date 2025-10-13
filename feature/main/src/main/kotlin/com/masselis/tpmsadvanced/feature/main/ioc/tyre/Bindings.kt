package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.usecase.ListenBoundTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreSmartDutyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreWithDatabaseUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.LocatedTyreScannerUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SearchSensorToBindUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SensorBindingUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.TyreAtmosphereUseCase
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @SingleIn(TyreComponent::class)
    @Provides
    private fun sensorBindingUseCase(
        currentVehicle: Vehicle,
        vehicleDatabase: VehicleDatabase,
        sensorDatabase: SensorDatabase,
        currentLocation: Location,
        scope: CoroutineScope,
    ): SensorBindingUseCase = SensorBindingUseCase(
        currentVehicle,
        vehicleDatabase,
        sensorDatabase,
        currentLocation,
        scope
    )

    @SingleIn(TyreComponent::class)
    @Provides
    private fun listenTyreWithDatabaseUseCase(
        vehicle: Vehicle,
        location: Location,
        tyreDatabase: TyreDatabase,
        listenTyreUseCase: ListenTyreSmartDutyUseCase,
        scope: CoroutineScope,
    ): ListenTyreWithDatabaseUseCase =
        ListenTyreWithDatabaseUseCase(vehicle, location, tyreDatabase, listenTyreUseCase, scope)

    @Provides
    private fun listenBoundTyreUseCase(
        listenTyreUseCase: ListenTyreWithDatabaseUseCase,
        sensorBindingUseCase: SensorBindingUseCase,
    ): ListenBoundTyreUseCase = ListenBoundTyreUseCase(listenTyreUseCase, sensorBindingUseCase)

    @Provides
    private fun listenTyreUseCase(uc: ListenBoundTyreUseCase): ListenTyreUseCase = uc

    @Provides
    private fun tyreAtmosphereUseCase(listenTyreUseCase: ListenTyreUseCase): TyreAtmosphereUseCase =
        TyreAtmosphereUseCase(listenTyreUseCase)

    @Provides
    private fun searchSensorToBindUseCase(
        listenTyreUseCase: ListenTyreUseCase,
        sensorBindingUseCase: SensorBindingUseCase,
    ): SearchSensorToBindUseCase =
        SearchSensorToBindUseCase(listenTyreUseCase, sensorBindingUseCase)

    @Provides
    private fun listenTyreSmartDutyUseCase(scanner: LocatedTyreScannerUseCase): ListenTyreSmartDutyUseCase =
        ListenTyreSmartDutyUseCase(scanner)

    @Provides
    private fun locatedTyreScannerUseCase(
        source: BluetoothLeScanner,
        currentLocation: Location,
        sensorBindingUseCase: SensorBindingUseCase,
    ): LocatedTyreScannerUseCase =
        LocatedTyreScannerUseCase(source, currentLocation, sensorBindingUseCase)
}

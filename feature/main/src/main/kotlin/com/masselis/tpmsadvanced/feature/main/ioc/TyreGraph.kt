package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.BindSensorButtonViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreStatsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.ListenBoundTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreSmartDutyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreWithDatabaseUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.LocatedTyreScannerUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SearchSensorToBindUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SensorBindingUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.TyreAtmosphereUseCase
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Extends
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope


public interface TyreGraph {
    public sealed interface Scope

    public val tyreAtmosphereUseCase: TyreAtmosphereUseCase

    public companion object :
            (InternalVehicleGraph, Location) -> TyreGraph by InternalTyreGraph.Factory
}

@Suppress("PropertyName", "VariableNaming", "unused")
@DependencyGraph(TyreGraph.Scope::class)
internal interface InternalTyreGraph : TyreGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Extends vehicleGraph: InternalVehicleGraph,
            @Provides @Named("base") vehicle: Vehicle,
            @Provides location: Location,
            @Provides scope: CoroutineScope,
        ): InternalTyreGraph

        companion object :
            Factory by createGraphFactory<Factory>(),
                (InternalVehicleGraph, Location) -> InternalTyreGraph {
            override fun invoke(
                vehicle: InternalVehicleGraph,
                location: Location
            ): InternalTyreGraph = vehicle.internalTyreGraph(location)
        }
    }

    val TyreViewModel: TyreViewModelImpl.Factory
    val TyreStatViewModel: TyreStatsViewModelImpl.Factory
    val BindSensorButtonViewModel: BindSensorButtonViewModelImpl.Factory

    @Provides
    @SingleIn(TyreGraph.Scope::class)
    private fun listenTyreWithDatabaseUseCase(
        @Named("base") vehicle: Vehicle,
        location: Location,
        tyreDatabase: TyreDatabase,
        listenTyreUseCase: ListenTyreSmartDutyUseCase,
        scope: CoroutineScope,
    ): ListenTyreWithDatabaseUseCase = ListenTyreWithDatabaseUseCase(
        vehicle,
        location,
        tyreDatabase,
        listenTyreUseCase,
        scope
    )

    @Provides
    private fun listenBoundTyreUseCase(
        listenTyreUseCase: ListenTyreWithDatabaseUseCase,
        sensorBindingUseCase: SensorBindingUseCase,
    ): ListenBoundTyreUseCase = ListenBoundTyreUseCase(listenTyreUseCase, sensorBindingUseCase)

    @Provides
    private fun listenTyreUseCase(uc: ListenBoundTyreUseCase): ListenTyreUseCase = uc

    @Provides
    private fun tyreAtmosphereUseCase(
        listenTyreUseCase: ListenTyreUseCase
    ): TyreAtmosphereUseCase = TyreAtmosphereUseCase(listenTyreUseCase)

    @Provides
    private fun listenTyreSmartDutyUseCase(
        scanner: LocatedTyreScannerUseCase,
    ): ListenTyreSmartDutyUseCase = ListenTyreSmartDutyUseCase(scanner)

    @Provides
    private fun locatedTyreScannerUseCase(
        source: BluetoothLeScanner,
        currentLocation: Location,
        sensorBindingUseCase: SensorBindingUseCase,
    ): LocatedTyreScannerUseCase = LocatedTyreScannerUseCase(
        source,
        currentLocation,
        sensorBindingUseCase
    )

    @Provides
    @SingleIn(TyreGraph.Scope::class)
    private fun sensorBindingUseCase(
        @Named("base") currentVehicle: Vehicle,
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

    @Provides
    private fun searchSensorToBindUseCase(
        listenTyreUseCase: ListenTyreUseCase,
        sensorBindingUseCase: SensorBindingUseCase,
    ): SearchSensorToBindUseCase = SearchSensorToBindUseCase(
        listenTyreUseCase,
        sensorBindingUseCase
    )
}

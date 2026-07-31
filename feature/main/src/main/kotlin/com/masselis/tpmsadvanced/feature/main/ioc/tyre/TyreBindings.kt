package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreIconViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.BindSensorButtonViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreIconViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.TyreStatsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleLifecycle
import com.masselis.tpmsadvanced.feature.main.usecase.ListenBoundTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreSmartDutyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.ListenTyreWithDatabaseUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.LocatedTyreScannerUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SearchSensorToBindUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SensorBindingUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope

@Suppress("unused", "MaxLineLength", "FunctionNaming")
@ContributesTo(scope = TyreComponent.Scope::class)
public interface TyreBindings {

    @SingleIn(TyreComponent.Scope::class)
    @Provides
    private fun sensorBindingUseCase(
        currentVehicle: Vehicle,
        vehicleDatabase: VehicleDatabase,
        sensorDatabase: SensorDatabase,
        currentLocation: Location,
        @VehicleLifecycle scope: CoroutineScope,
    ): SensorBindingUseCase = SensorBindingUseCase(
        currentVehicle,
        vehicleDatabase,
        sensorDatabase,
        currentLocation,
        scope
    )

    @SingleIn(TyreComponent.Scope::class)
    @Provides
    private fun listenTyreWithDatabaseUseCase(
        vehicle: Vehicle,
        location: Location,
        tyreDatabase: TyreDatabase,
        listenTyreUseCase: ListenTyreSmartDutyUseCase,
        @VehicleLifecycle scope: CoroutineScope,
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

    @SingleIn(TyreComponent.Scope::class)
    @Provides
    private fun tyreStatsUseCase(
        atmosphereUseCase: TyreAtmosphereUseCase,
        rangeUseCase: VehicleRangesUseCase,
        unitPreferences: UnitPreferences,
        @VehicleLifecycle scope: CoroutineScope,
    ): TyreStatsStateFlow = TyreStatsStateFlow(
        atmosphereUseCase,
        rangeUseCase,
        unitPreferences,
        scope
    )

    @SingleIn(TyreComponent.Scope::class)
    @Provides
    private fun tyreIconUseCase(
        atmosphereUseCase: TyreAtmosphereUseCase,
        rangeUseCase: VehicleRangesUseCase,
        @VehicleLifecycle scope: CoroutineScope,
    ): TyreIconStateFlow = TyreIconStateFlow(
        atmosphereUseCase,
        rangeUseCase,
        scope
    )

    @Provides
    private fun tyreStatsViewModelImpl(uc: TyreStatsStateFlow): TyreStatsViewModelImpl =
        TyreStatsViewModelImpl(uc)

    @Provides
    private fun tyreStatsViewModel(vm: TyreStatsViewModelImpl): TyreStatsViewModel = vm

    @Provides
    private fun tyreIconViewModelImpl(uc: TyreIconStateFlow): TyreIconViewModelImpl =
        TyreIconViewModelImpl(uc)

    @Provides
    private fun tyreIconViewModel(vm: TyreIconViewModelImpl): TyreIconViewModel = vm

    public val internal: Internal

    @Inject
    public class Internal internal constructor(
        internal val tyreIconViewModel: () -> TyreIconViewModelImpl,
        internal val tyreStatsViewModel: () -> TyreStatsViewModelImpl,
        internal val bindSensorButtonViewModel: BindSensorButtonViewModelImpl.Factory,
    )

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    public companion object {
        private val TyreComponent.internal
            get() = (this as TyreBindings).internal

        internal fun TyreComponent.TyreIconViewModel() = internal.tyreIconViewModel()
        internal fun TyreComponent.TyreStatsViewModel() = internal.tyreStatsViewModel()
        internal val TyreComponent.BindSensorButtonViewModel get() = internal.bindSensorButtonViewModel
    }
}

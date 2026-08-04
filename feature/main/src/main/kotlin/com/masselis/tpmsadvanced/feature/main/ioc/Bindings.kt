package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.CurrentVehicleDropdownViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleComponentCacheUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused", "FunctionNaming")
@ContributesTo(AppScope::class)
public interface Bindings {

    @SingleIn(AppScope::class)
    @Provides
    private fun currentVehicleUseCase(vehicleDatabase: VehicleDatabase): CurrentVehicleUseCase =
        CurrentVehicleUseCase(vehicleDatabase)

    @SingleIn(AppScope::class)
    @Provides
    private fun noveltyUseCase(appPreferences: AppPreferences): NoveltyUseCase =
        NoveltyUseCase(appPreferences)

    @Provides
    private fun vehicleListUseCase(vehicleDatabase: VehicleDatabase): VehicleListUseCase =
        VehicleListUseCase(vehicleDatabase)

    @Provides
    private fun vehicleCountStateFlowUseCase(vehicleDatabase: VehicleDatabase): VehicleCountStateFlowUseCase =
        VehicleCountStateFlowUseCase(vehicleDatabase)

    @Provides
    private fun preconditionsViewModel(bluetoothLeScanner: BluetoothLeScanner): PreconditionsViewModel =
        PreconditionsViewModel(bluetoothLeScanner)

    @SingleIn(AppScope::class)
    @Provides
    private fun vehicleComponentCacheUseCase(
        vehicleDatabase: VehicleDatabase,
        factory: VehicleComponent.Factory,
    ): VehicleComponentCacheUseCase = VehicleComponentCacheUseCase(
        vehicleDatabase,
        factory
    )


    public val featureMainInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val vehicleComponentCache: () -> VehicleComponentCacheUseCase,
        internal val preconditionsViewModel: () -> PreconditionsViewModel,
        internal val currentVehicleDropdownViewModel: CurrentVehicleDropdownViewModelImpl.Factory,
    )

    public companion object : Bindings by appGraph as Bindings {
        internal val vehicleComponentCache get() = featureMainInternal.vehicleComponentCache()
        internal fun PreconditionsViewModel() = featureMainInternal.preconditionsViewModel()
        internal val CurrentVehicleDropdownViewModel
            get() = featureMainInternal.currentVehicleDropdownViewModel
    }
}

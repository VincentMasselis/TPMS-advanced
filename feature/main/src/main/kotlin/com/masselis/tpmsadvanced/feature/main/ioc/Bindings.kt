package com.masselis.tpmsadvanced.feature.main.ioc

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.DemoModeSwitchViewModel
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
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@Suppress("unused")
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
    @IntoMap
    @ViewModelKey(PreconditionsViewModel::class)
    private fun preconditionsViewModel(bluetoothLeScanner: BluetoothLeScanner): ViewModel =
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

    @Provides
    @IntoMap
    @ViewModelKey(DemoModeSwitchViewModel::class)
    private fun demoModeSwitchViewModel(demoOrBleScannerUseCase: DemoOrBleScannerUseCase): ViewModel =
        DemoModeSwitchViewModel(demoOrBleScannerUseCase)

    @Provides
    @IntoMap
    @ViewModelAssistedFactoryKey(CurrentVehicleDropdownViewModelImpl::class)
    private fun currentVehicleDropdownViewModelFactory(
        factory: CurrentVehicleDropdownViewModelImpl.Factory,
    ): ViewModelAssistedFactory = factory

    public val featureMainInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val vehicleComponentCache: () -> VehicleComponentCacheUseCase,
    )

    public companion object : Bindings by appGraph as Bindings {
        internal val vehicleComponentCache get() = featureMainInternal.vehicleComponentCache()
    }
}

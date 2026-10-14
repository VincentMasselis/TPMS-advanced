package com.masselis.tpmsadvanced.feature.unlocated.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.BindDialogViewModelImpl
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.ListSensorViewModelImpl
import com.masselis.tpmsadvanced.feature.unlocated.usecase.BindSensorToVehicleUseCase
import com.masselis.tpmsadvanced.feature.unlocated.usecase.VehicleBindingStatusUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey

@Suppress("unused")
@BindingContainer
@ContributesTo(AppScope::class)
public object Bindings {
    @Provides
    private fun vehicleBindingStatusUseCase(
        vehicleDatabase: VehicleDatabase,
        sensorDatabase: SensorDatabase,
    ): VehicleBindingStatusUseCase = VehicleBindingStatusUseCase(vehicleDatabase, sensorDatabase)

    @Provides
    private fun bindSensorToVehicleUseCase(
        demoOrBleScannerUseCase: DemoOrBleScannerUseCase,
        sensorDatabase: SensorDatabase,
        tyreDatabase: TyreDatabase,
    ): BindSensorToVehicleUseCase =
        if (demoOrBleScannerUseCase.isDemo.value) BindSensorToVehicleUseCase.NoOp
        else BindSensorToVehicleUseCase.Impl(sensorDatabase, tyreDatabase)

    @Provides
    @IntoMap
    @ManualViewModelAssistedFactoryKey(ListSensorViewModelImpl.Factory::class)
    private fun listSensorViewModelFactory(factory: ListSensorViewModelImpl.Factory): ManualViewModelAssistedFactory =
        factory

    @Provides
    @IntoMap
    @ManualViewModelAssistedFactoryKey(BindDialogViewModelImpl.Factory::class)
    private fun bindDialogViewModelFactory(factory: BindDialogViewModelImpl.Factory): ManualViewModelAssistedFactory =
        factory
}

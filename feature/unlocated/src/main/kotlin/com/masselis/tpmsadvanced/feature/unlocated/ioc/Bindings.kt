package com.masselis.tpmsadvanced.feature.unlocated.ioc

import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.usecase.DemoOrBleScannerUseCase
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.BindDialogViewModelImpl
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.ListSensorViewModelImpl
import com.masselis.tpmsadvanced.feature.unlocated.usecase.BindSensorToVehicleUseCase
import com.masselis.tpmsadvanced.feature.unlocated.usecase.VehicleBindingStatusUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface Bindings {
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

    public val featureUnlocatedInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val listSensorViewModel: ListSensorViewModelImpl.Factory,
        internal val bindDialogViewModel: BindDialogViewModelImpl.Factory
    )

    public companion object : Bindings by appGraph as Bindings {
        internal val ListSensorViewModel = featureUnlocatedInternal.listSensorViewModel
        internal val BindDialogViewModel = featureUnlocatedInternal.bindDialogViewModel
    }
}

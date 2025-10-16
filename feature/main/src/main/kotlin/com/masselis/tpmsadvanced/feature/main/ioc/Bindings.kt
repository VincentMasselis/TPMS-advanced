package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.InternalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleComponentCacheUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@BindingContainer
internal interface Bindings {

    @Binds
    val VehicleComponentCacheUseCase.bind: (Vehicle) -> InternalVehicleComponent

    companion object {
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
        private fun preconditionsViewModel(bluetoothLeScanner: BluetoothLeScanner): PreconditionsViewModel =
            PreconditionsViewModel(bluetoothLeScanner)

        @SingleIn(AppScope::class)
        @Provides
        private fun vehicleComponentCacheUseCase(
            vehicleDatabase: VehicleDatabase,
            vehicleComponentFactory: InternalVehicleComponent.Factory
        ): VehicleComponentCacheUseCase =
            VehicleComponentCacheUseCase(vehicleDatabase, vehicleComponentFactory)

        @Provides
        private fun vehicleCountStateFlowUseCase(vehicleDatabase: VehicleDatabase): VehicleCountStateFlowUseCase =
            VehicleCountStateFlowUseCase(vehicleDatabase)
    }
}

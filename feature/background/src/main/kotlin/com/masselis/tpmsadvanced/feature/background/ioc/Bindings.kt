package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.ForegroundServiceUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@BindingContainer
internal object Bindings {

    @Provides
    @SingleIn(AppScope::class)
    private fun checkForPermissionUseCase(database: VehicleDatabase): CheckForPermissionUseCase =
        CheckForPermissionUseCase(database)

    @Provides
    @SingleIn(AppScope::class)
    private fun foregroundServiceUseCase(
        vehiclesToMonitorUseCase: VehiclesToMonitorUseCase,
        checkForPermissionUseCase: CheckForPermissionUseCase,
    ): ForegroundServiceUseCase =
        ForegroundServiceUseCase(vehiclesToMonitorUseCase, checkForPermissionUseCase)

    @Provides
    @SingleIn(AppScope::class)
    private fun vehiclesToMonitorUseCase(
        currentVehicleUseCase: CurrentVehicleUseCase,
        vehicleDatabase: VehicleDatabase,
    ): VehiclesToMonitorUseCase = VehiclesToMonitorUseCase(currentVehicleUseCase, vehicleDatabase)
}

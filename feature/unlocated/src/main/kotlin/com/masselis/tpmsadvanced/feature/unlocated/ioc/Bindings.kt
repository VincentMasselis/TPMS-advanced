package com.masselis.tpmsadvanced.feature.unlocated.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.unlocated.usecase.BindSensorToVehicleUseCase
import com.masselis.tpmsadvanced.feature.unlocated.usecase.VehicleBindingStatusUseCase
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @Provides
    private fun vehicleBindingStatusUseCase(
        vehicleDatabase: VehicleDatabase,
        sensorDatabase: SensorDatabase,
    ): VehicleBindingStatusUseCase = VehicleBindingStatusUseCase(vehicleDatabase, sensorDatabase)

    @Provides
    private fun bindSensorToVehicleUseCase(
        sensorDatabase: SensorDatabase,
        tyreDatabase: TyreDatabase,
    ): BindSensorToVehicleUseCase = BindSensorToVehicleUseCase(sensorDatabase, tyreDatabase)
}

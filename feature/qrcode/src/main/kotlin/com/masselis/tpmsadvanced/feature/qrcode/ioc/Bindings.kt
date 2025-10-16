package com.masselis.tpmsadvanced.feature.qrcode.ioc

import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.CameraAnalyser
import com.masselis.tpmsadvanced.feature.qrcode.usecase.BoundSensorMapUseCase
import com.masselis.tpmsadvanced.feature.qrcode.usecase.QrCodeSensorUseCase
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@Suppress("unused")
@BindingContainer
internal object Bindings {

    @Provides
    private fun cameraAnalyser(): CameraAnalyser = CameraAnalyser()

    @Provides
    private fun boundSensorMapUseCase(
        sensorDatabase: SensorDatabase,
        currentVehicleUseCase: CurrentVehicleUseCase,
    ): BoundSensorMapUseCase = BoundSensorMapUseCase(sensorDatabase, currentVehicleUseCase)

    @Provides
    private fun qrCodeSensorUseCase(
        cameraAnalyser: CameraAnalyser,
        currentVehicleUseCase: CurrentVehicleUseCase
    ): QrCodeSensorUseCase = QrCodeSensorUseCase(cameraAnalyser, currentVehicleUseCase)
}

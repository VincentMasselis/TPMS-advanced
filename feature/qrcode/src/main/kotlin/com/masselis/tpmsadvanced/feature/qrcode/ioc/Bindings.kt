package com.masselis.tpmsadvanced.feature.qrcode.ioc

import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.CameraAnalyser
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.QRCodeViewModel
import com.masselis.tpmsadvanced.feature.qrcode.usecase.BoundSensorMapUseCase
import com.masselis.tpmsadvanced.feature.qrcode.usecase.QrCodeSensorUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface Bindings {

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

    public val featureQrCodeInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val qrCodeViewModel: QRCodeViewModel.Factory
    )

   public companion object {
        internal val QrCodeViewModel
            get() = (appGraph as Bindings).featureQrCodeInternal.qrCodeViewModel
    }
}

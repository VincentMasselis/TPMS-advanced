package com.masselis.tpmsadvanced.feature.background.ioc.vehicle

import android.app.Service
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreComponent
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @Provides
    @SingleIn(BackgroundVehicleComponent::class)
    private fun serviceNotifier(
        vehicle: Vehicle,
        scope: CoroutineScope,
        tyreComponent: (Vehicle.Kind.Location) -> TyreComponent,
        vehicleRangesUseCase: VehicleRangesUseCase,
        unitPreferences: UnitPreferences,
        foregroundService: Service?,
    ): ServiceNotifier = ServiceNotifier(
        vehicle,
        scope,
        tyreComponent,
        vehicleRangesUseCase,
        unitPreferences,
        foregroundService
    )
}

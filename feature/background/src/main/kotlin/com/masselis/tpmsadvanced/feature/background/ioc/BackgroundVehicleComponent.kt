package com.masselis.tpmsadvanced.feature.background.ioc

import android.app.Service
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named

@BackgroundVehicleComponent.Scope
@Component(
    dependencies = [
        VehicleComponent::class,
        DataUnitComponent::class,
        FeatureBackgroundComponent::class,
    ],
)
internal interface BackgroundVehicleComponent {
    @Component.Factory
    interface Factory {
        fun build(
            @BindsInstance foregroundService: Service?,
            vehicleComponent: VehicleComponent,
            @BindsInstance scope: CoroutineScope,
            dataUnitComponent: DataUnitComponent,
            featureBackgroundComponent: FeatureBackgroundComponent,
        ): BackgroundVehicleComponent
    }

    @javax.inject.Scope
    annotation class Scope

    @get:Named("base")
    val vehicle: Vehicle

    val scope: CoroutineScope
    val foregroundService: Service?

    val serviceNotifier: ServiceNotifier

    companion object : (Service?, Vehicle) -> BackgroundVehicleComponent {
        override fun invoke(
            foregroundService: Service?,
            vehicle: Vehicle
        ): BackgroundVehicleComponent = DaggerBackgroundVehicleComponent
            .factory()
            .build(
                foregroundService,
                VehicleComponent(vehicle),
                CoroutineScope(SupervisorJob()),
                DataUnitComponent,
                FeatureBackgroundComponent
            )
            .apply { serviceNotifier } // Creates an instance of `ServiceNotifier` after build.
    }
}

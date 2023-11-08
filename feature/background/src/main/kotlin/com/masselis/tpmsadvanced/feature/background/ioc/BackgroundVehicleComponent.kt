package com.masselis.tpmsadvanced.feature.background.ioc

import android.app.Service
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
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
internal abstract class BackgroundVehicleComponent {
    @Component.Factory
    protected abstract class Factory {
        abstract fun build(
            @BindsInstance foregroundService: Service?,
            vehicleComponent: VehicleComponent,
            @BindsInstance scope: CoroutineScope = CoroutineScope(SupervisorJob()),
            dataUnitComponent: DataUnitComponent = DataUnitComponent,
            featureBackgroundComponent: FeatureBackgroundComponent = FeatureBackgroundComponent,
        ): BackgroundVehicleComponent
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    abstract val vehicle: Vehicle

    abstract val scope: CoroutineScope
    abstract val foregroundService: Service?

    abstract val serviceNotifier: ServiceNotifier

    companion object : (Service?, Vehicle) -> BackgroundVehicleComponent {
        override fun invoke(
            foregroundService: Service?,
            vehicle: Vehicle
        ): BackgroundVehicleComponent = DaggerBackgroundVehicleComponent
            .factory()
            .build(
                foregroundService,
                VehicleComponent(vehicle)
            )
            .apply { serviceNotifier } // Creates an instance of `ServiceNotifier` after build.
    }
}

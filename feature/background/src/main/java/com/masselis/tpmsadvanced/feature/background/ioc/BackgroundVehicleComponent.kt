package com.masselis.tpmsadvanced.feature.background.ioc

import android.app.Service
import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@BackgroundVehicleComponent.Scope
@Component(
    dependencies = [
        VehicleComponent::class,
        DataUnitComponent::class,
        FeatureBackgroundComponent::class,
    ],
    modules = [
        BackgroundVehicleModule::class
    ]
)
internal abstract class BackgroundVehicleComponent {

    @Component.Factory
    abstract class Factory {
        @InternalDaggerImplementation
        abstract fun daggerOnlyBuild(
            @BindsInstance foregroundService: Service?,
            vehicleComponent: VehicleComponent,
            dataUnitComponent: DataUnitComponent = DataUnitComponent,
            featureBackgroundComponent: FeatureBackgroundComponent = FeatureBackgroundComponent,
        ): BackgroundVehicleComponent

        @OptIn(InternalDaggerImplementation::class)
        fun build(
            foregroundService: Service?,
            vehicleComponent: VehicleComponent
        ) = daggerOnlyBuild(foregroundService, vehicleComponent)
            .apply { serviceNotifier } // Creates an instance of `ServiceNotifier` after build.
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    abstract val vehicle: Vehicle

    @get:Named("background_vehicle_component")
    abstract val release: () -> Unit
    abstract val foregroundService: Service?

    abstract val serviceNotifier: ServiceNotifier
}
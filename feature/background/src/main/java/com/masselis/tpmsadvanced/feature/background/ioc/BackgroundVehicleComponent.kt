package com.masselis.tpmsadvanced.feature.background.ioc

import android.app.Service
import com.masselis.tpmsadvanced.core.common.InternalDaggerImplementation
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
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
    abstract class Factory {
        @InternalDaggerImplementation
        abstract fun daggerOnlyBuild(
            @BindsInstance foregroundService: Service?,
            vehicleComponent: VehicleComponent,
            @BindsInstance scope: CoroutineScope = CoroutineScope(SupervisorJob()),
            dataUnitComponent: DataUnitComponent = DataUnitComponent,
            featureBackgroundComponent: FeatureBackgroundComponent = FeatureBackgroundComponent,
        ): BackgroundVehicleComponent

        @OptIn(InternalDaggerImplementation::class)
        fun build(
            foregroundService: Service?,
            vehicle: Vehicle
        ) = daggerOnlyBuild(
            foregroundService,
            FeatureCoreComponent.vehicleComponentFactory.build(vehicle)
        ).apply { serviceNotifier } // Creates an instance of `ServiceNotifier` after build.
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    abstract val vehicle: Vehicle

    abstract val scope: CoroutineScope
    abstract val foregroundService: Service?

    abstract val serviceNotifier: ServiceNotifier
}

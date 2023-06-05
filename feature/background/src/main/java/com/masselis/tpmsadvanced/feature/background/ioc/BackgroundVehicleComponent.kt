package com.masselis.tpmsadvanced.feature.background.ioc

import android.app.Service
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named

@BackgroundVehicleComponent.Scope
@Component(
    dependencies = [VehicleComponent::class]
)
internal abstract class BackgroundVehicleComponent {

    @Component.Factory
    interface Factory {
        fun build(
            vehicleComponent: VehicleComponent,
            @BindsInstance service: Service,
        ): BackgroundVehicleComponent
    }

    @javax.inject.Scope
    internal annotation class Scope

    @get:Named("base")
    abstract val vehicle: Vehicle
    abstract val scope: CoroutineScope

    abstract val serviceNotifier: ServiceNotifier
}
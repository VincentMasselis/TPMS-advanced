package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.background.interfaces.DisableMonitorBroadcastReceiver
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.AutomaticBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.ManualBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.ForegroundServiceUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import dagger.Component
import javax.inject.Inject

@FeatureBackgroundComponent.Scope
@Component(
    dependencies = [
        DataVehicleComponent::class,
        FeatureCoreComponent::class
    ]
)
public interface FeatureBackgroundComponent {
    @Component.Factory
    public abstract class Factory {
        internal abstract fun build(
            dataVehicleComponent: DataVehicleComponent,
            featureCoreComponent: FeatureCoreComponent
        ): FeatureBackgroundComponent
    }

    @javax.inject.Scope
    public annotation class Scope

    public fun inject(injectable: Injectable)

    public fun inject(injectable: MonitorService)

    public fun inject(disableMonitorBroadcastReceiver: DisableMonitorBroadcastReceiver)

    public val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    public companion object : Injectable()

    @Suppress("PropertyName", "VariableNaming")
    public abstract class Injectable protected constructor() :
        FeatureBackgroundComponent by DaggerFeatureBackgroundComponent
            .factory()
            .build(DataVehicleComponent, FeatureCoreComponent) {

        // Theses use cases do stuff in background when initialized
        @Inject
        internal lateinit var checkForPermissionUseCase: CheckForPermissionUseCase

        @Inject
        internal lateinit var foregroundServiceUseCase: ForegroundServiceUseCase

        // View models
        @Inject
        internal lateinit var AutomaticBackgroundViewModel: AutomaticBackgroundViewModel.Factory

        @Inject
        internal lateinit var ManualBackgroundViewModel: ManualBackgroundViewModel.Factory

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataVehicleComponent
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
    public interface Factory {
        public fun build(
            dataVehicleComponent: DataVehicleComponent = DataVehicleComponent,
            featureCoreComponent: FeatureCoreComponent = FeatureCoreComponent
        ): FeatureBackgroundComponent
    }

    @javax.inject.Scope
    public annotation class Scope

    public fun inject(injectable: Injectable)

    public fun inject(injectable: MonitorService)

    public fun inject(disableMonitorBroadcastReceiver: DisableMonitorBroadcastReceiver)

    public val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    public companion object : Injectable()

    public abstract class Injectable protected constructor() :
        FeatureBackgroundComponent by DaggerFeatureBackgroundComponent.factory()
            .build() {

        @Inject
        internal lateinit var checkForPermissionUseCase: CheckForPermissionUseCase

        @Inject
        internal lateinit var foregroundServiceUseCase: ForegroundServiceUseCase

        @Inject
        internal lateinit var automaticBackgroundViewModel: AutomaticBackgroundViewModel.Factory

        @Inject
        internal lateinit var manualBackgroundViewModel: ManualBackgroundViewModel.Factory

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.background.interfaces.BackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
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

    public companion object : Injectable()

    public abstract class Injectable protected constructor() :
        FeatureBackgroundComponent by DaggerFeatureBackgroundComponent.factory()
            .build() {

        @Inject
        internal lateinit var checkForPermissionUseCase: CheckForPermissionUseCase

        @Inject
        internal lateinit var backgroundViewModel: BackgroundViewModel.Factory

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

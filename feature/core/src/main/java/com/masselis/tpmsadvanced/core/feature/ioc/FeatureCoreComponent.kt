package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CarListDropdownViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentCarComponentViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentCarTextViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.unit.ioc.FeatureUnitComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataCarComponent
import com.masselis.tpmsadvanced.data.record.ioc.DataRecordComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import dagger.Component
import javax.inject.Inject

@SingleInstance
@Component(
    modules = [
        CarComponentModule::class
    ],
    dependencies = [
        CoreCommonComponent::class,
        DataRecordComponent::class,
        DataUnitComponent::class,
        DataCarComponent::class,
        FeatureUnitComponent::class,
    ]
)
public interface FeatureCoreComponent {

    @Component.Factory
    public interface Factory {
        public fun build(
            coreCommonComponent: CoreCommonComponent,
            dataRecordComponent: DataRecordComponent,
            dataUnitComponent: DataUnitComponent,
            dataCarComponent: DataCarComponent,
            featureUnitComponent: FeatureUnitComponent,
        ): FeatureCoreComponent
    }

    public fun inject(injectable: Injectable)

    public companion object : Injectable()

    public abstract class Injectable protected constructor() :
        FeatureCoreComponent by DaggerFeatureCoreComponent
            .factory()
            .build(
                CoreCommonComponent,
                DataRecordComponent,
                DataUnitComponent,
                DataCarComponent,
                FeatureUnitComponent
            ) {

        @Inject
        internal lateinit var preconditionsViewModel: PreconditionsViewModel.Factory

        @Inject
        internal lateinit var currentCarTextViewModel: CurrentCarTextViewModel

        @Inject
        internal lateinit var carListDropdownViewModel: CarListDropdownViewModel

        @Inject
        internal lateinit var currentCarComponentViewModel: CurrentCarComponentViewModel

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

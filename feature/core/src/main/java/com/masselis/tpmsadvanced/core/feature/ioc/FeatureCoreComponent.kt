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
public abstract class FeatureCoreComponent {

    @Component.Factory
    internal abstract class Factory {
        abstract fun build(
            coreCommonComponent: CoreCommonComponent,
            dataRecordComponent: DataRecordComponent,
            dataUnitComponent: DataUnitComponent,
            dataCarComponent: DataCarComponent,
            featureUnitComponent: FeatureUnitComponent,
        ): FeatureCoreComponent
    }

    internal abstract val preconditionsViewModel: PreconditionsViewModel.Factory
    internal abstract val currentCarTextViewModel: CurrentCarTextViewModel
    internal abstract val carListDropdownViewModel: CarListDropdownViewModel
    internal abstract val currentCarComponentViewModel: CurrentCarComponentViewModel
}

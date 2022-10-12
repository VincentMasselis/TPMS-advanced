package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CarListViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.FavouriteCarComponentViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.FavouriteCarViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.SettingsViewModel
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
    internal abstract val favouriteCarViewModel: FavouriteCarViewModel
    internal abstract val carListViewModel: CarListViewModel
    internal abstract val favouriteCarComponentViewModel: FavouriteCarComponentViewModel
}

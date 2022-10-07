package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentCarViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.SettingsViewModel
import com.masselis.tpmsadvanced.core.feature.unit.ioc.FeatureUnitComponent
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.ioc.DataCarComponent
import com.masselis.tpmsadvanced.data.favourite.ioc.DataFavouriteComponent
import com.masselis.tpmsadvanced.data.record.ioc.DataRecordComponent
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import dagger.Component

@SingleInstance
@Component(
    modules = [
        TyreComponentFactoryModule::class,
    ],
    dependencies = [
        CoreCommonComponent::class,
        DataRecordComponent::class,
        DataUnitComponent::class,
        DataFavouriteComponent::class,
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
            dataFavouriteComponent: DataFavouriteComponent,
            dataCarComponent: DataCarComponent,
            featureUnitComponent: FeatureUnitComponent,
        ): FeatureCoreComponent
    }

    protected abstract val findTyreComponentUseCase: FindTyreComponentUseCase
    internal fun tyreComponent(location: TyreLocation) = findTyreComponentUseCase.find(location)

    internal abstract val preconditionsViewModel: PreconditionsViewModel.Factory
    internal abstract val settingsViewModel: SettingsViewModel.Factory
    internal abstract val clearFavouritesViewModel: ClearFavouritesViewModel.Factory
    internal abstract val currentCarViewModel: CurrentCarViewModel
}

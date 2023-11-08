package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleDropdownViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import dagger.Component
import javax.inject.Inject
import javax.inject.Provider

@Suppress("PropertyName")
@FeatureCoreComponent.Scope
@Component(
    modules = [
        VehicleComponentModule::class
    ],
    dependencies = [
        CoreCommonComponent::class,
        DataUnitComponent::class,
        DataVehicleComponent::class,
        DataAppComponent::class,
    ]
)
public interface FeatureCoreComponent {

    @Component.Factory
    public interface Factory {
        public fun build(
            coreCommonComponent: CoreCommonComponent = CoreCommonComponent,
            dataUnitComponent: DataUnitComponent = DataUnitComponent,
            dataVehicleComponent: DataVehicleComponent = DataVehicleComponent,
            dataAppComponent: DataAppComponent = DataAppComponent,
        ): FeatureCoreComponent
    }

    @javax.inject.Scope
    public annotation class Scope

    public val currentVehicleUseCase: CurrentVehicleUseCase
    public val noveltyUseCase: NoveltyUseCase
    public val vehicleListUseCase: VehicleListUseCase

    public fun inject(injectable: Injectable)
    public fun inject(injectable: VehicleComponent.Injectable)

    public companion object : Injectable()

    @Suppress("PropertyName", "VariableNaming")
    public abstract class Injectable protected constructor() :
        FeatureCoreComponent by DaggerFeatureCoreComponent
            .factory()
            .build() {

        @Inject
        internal lateinit var PreconditionsViewModel: Provider<PreconditionsViewModel>

        @Inject
        internal lateinit var CurrentVehicleDropdownViewModel: CurrentVehicleDropdownViewModel.Factory

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }
}

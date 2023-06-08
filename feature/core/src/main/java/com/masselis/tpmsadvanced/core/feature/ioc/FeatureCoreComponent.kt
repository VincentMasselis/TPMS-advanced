package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleComponentViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CurrentVehicleDropdownViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import com.masselis.tpmsadvanced.data.car.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.data.record.ioc.DataRecordComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import dagger.Component
import javax.inject.Inject

@FeatureCoreComponent.Scope
@Component(
    modules = [
        VehicleComponentModule::class
    ],
    dependencies = [
        CoreCommonComponent::class,
        DataRecordComponent::class,
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
            dataRecordComponent: DataRecordComponent = DataRecordComponent,
            dataUnitComponent: DataUnitComponent = DataUnitComponent,
            dataVehicleComponent: DataVehicleComponent = DataVehicleComponent,
            dataAppComponent: DataAppComponent = DataAppComponent,
        ): FeatureCoreComponent
    }

    @javax.inject.Scope
    public annotation class Scope

    public val noveltyUseCase: NoveltyUseCase

    public val vehicleComponentFactory: VehicleComponent.Factory

    public fun inject(injectable: Injectable)

    public companion object : Injectable()

    public abstract class Injectable protected constructor() :
        FeatureCoreComponent by DaggerFeatureCoreComponent
            .factory()
            .build() {

        @Inject
        internal lateinit var preconditionsViewModel: PreconditionsViewModel.Factory

        @Inject
        internal lateinit var currentVehicleDropdownViewModel: CurrentVehicleDropdownViewModel.Factory

        @Inject
        internal lateinit var currentVehicleComponentViewModel: CurrentVehicleComponentViewModel.Factory

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

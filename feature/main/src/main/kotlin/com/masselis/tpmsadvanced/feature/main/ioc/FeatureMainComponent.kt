package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.PreconditionsViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.CurrentVehicleDropdownViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.InternalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

public interface FeatureMainComponent {

    public val currentVehicleUseCase: CurrentVehicleUseCase
    public val noveltyUseCase: NoveltyUseCase
    public val vehicleListUseCase: VehicleListUseCase

    public companion object : FeatureMainComponent by InternalComponent
}

@Suppress("PropertyName", "VariableNaming")
@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalComponent : FeatureMainComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes coreCommonComponent: CoreCommonComponent,
            @Includes dataUnitComponent: DataUnitComponent,
            @Includes dataVehicleComponent: DataVehicleComponent,
            @Includes dataAppComponent: DataAppComponent,
        ): InternalComponent
    }

    fun PreconditionsViewModel(): PreconditionsViewModel
    val CurrentVehicleDropdownViewModel: CurrentVehicleDropdownViewModelImpl.Factory

    val VehicleComponentFactory: (Vehicle) -> InternalVehicleComponent
    val vehicleFactory: InternalVehicleComponent.Factory

    companion object : InternalComponent by createGraphFactory<Factory>().build(
        CoreCommonComponent,
        DataUnitComponent,
        DataVehicleComponent,
        DataAppComponent,
    )
}

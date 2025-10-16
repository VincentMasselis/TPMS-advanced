package com.masselis.tpmsadvanced.feature.unlocated.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.BindDialogViewModelImpl
import com.masselis.tpmsadvanced.feature.unlocated.interfaces.viewmodel.ListSensorViewModelImpl
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

@Suppress("PropertyName", "VariableNaming")
@DependencyGraph(
    bindingContainers = [Bindings::class]
)
internal interface FeatureUnlocatedComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes dataUnitComponent: DataUnitComponent,
            @Includes dataVehicleComponent: DataVehicleComponent,
            @Includes featureMainComponent: FeatureMainComponent
        ): FeatureUnlocatedComponent
    }

    val ListSensorViewModel: ListSensorViewModelImpl.Factory
    val BindDialogViewModel: BindDialogViewModelImpl.Factory

    companion object : FeatureUnlocatedComponent by createGraphFactory<Factory>().build(
        DataUnitComponent,
        DataVehicleComponent,
        FeatureMainComponent
    )
}

package com.masselis.tpmsadvanced.feature.unit.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.feature.unit.interfaces.UnitsViewModel
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

@DependencyGraph(
    bindingContainers = [Bindings::class]
)
internal interface FeatureUnitComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes dataUnitComponent: DataUnitComponent): FeatureUnitComponent
    }

    fun UnitsViewModel(): UnitsViewModel

    companion object : FeatureUnitComponent by createGraphFactory<Factory>().build(
        DataUnitComponent
    )
}

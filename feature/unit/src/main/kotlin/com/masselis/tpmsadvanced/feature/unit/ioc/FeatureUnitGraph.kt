package com.masselis.tpmsadvanced.feature.unit.ioc

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitGraph
import com.masselis.tpmsadvanced.feature.unit.interfaces.UnitsViewModel
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory

internal interface FeatureUnitGraph {
    fun UnitsViewModel(): UnitsViewModel

    companion object : InternalGraph by InternalGraph.Factory.build(DataUnitGraph)
}

@DependencyGraph
internal interface InternalGraph : FeatureUnitGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes dataUnitGraph: DataUnitGraph): InternalGraph

        companion object : Factory by createGraphFactory()
    }

    @Provides
    private fun unitsViewModel(unitPreferences: UnitPreferences): UnitsViewModel =
        UnitsViewModel(unitPreferences)
}

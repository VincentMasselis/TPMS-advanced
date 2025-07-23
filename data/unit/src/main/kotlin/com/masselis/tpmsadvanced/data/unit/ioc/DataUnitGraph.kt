package com.masselis.tpmsadvanced.data.unit.ioc

import android.content.Context
import com.masselis.tpmsadvanced.core.common.CoreCommonGraph
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory

public interface DataUnitGraph {
    public fun unitPreferences(): UnitPreferences

    public companion object : InternalGraph by InternalGraph.Factory.build(CoreCommonGraph)
}

@DependencyGraph(AppScope::class)
internal interface InternalGraph : DataUnitGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes coreCommonGraph: CoreCommonGraph): InternalGraph

        companion object : Factory by createGraphFactory()
    }

    @SingleIn(AppScope::class)
    @Provides
    private fun unitPreferences(context: Context): UnitPreferences = UnitPreferences(context)
}

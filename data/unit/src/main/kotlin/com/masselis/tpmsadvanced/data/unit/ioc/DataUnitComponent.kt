package com.masselis.tpmsadvanced.data.unit.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

public interface DataUnitComponent {
    public val unitPreferences: UnitPreferences

    public companion object : DataUnitComponent by InternalComponent
}

@Suppress("unused")
@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalComponent : DataUnitComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes coreCommonComponent: CoreCommonComponent): InternalComponent
    }

    companion object : InternalComponent by createGraphFactory<Factory>().build(CoreCommonComponent)
}

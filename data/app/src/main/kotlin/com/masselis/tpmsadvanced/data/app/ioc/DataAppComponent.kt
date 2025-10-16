package com.masselis.tpmsadvanced.data.app.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

public interface DataAppComponent {
    public val appPreferences: AppPreferences

    public companion object : DataAppComponent by InternalComponent
}

@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalComponent : DataAppComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes coreCommonComponent: CoreCommonComponent): InternalComponent
    }

    companion object : InternalComponent by createGraphFactory<Factory>().build(CoreCommonComponent)
}

package com.masselis.tpmsadvanced.ioc

import android.content.Context
import com.masselis.tpmsadvanced.core.common.NoDependencyInitializer
import com.masselis.tpmsadvanced.core.common.appGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory

@DependencyGraph(AppScope::class)
internal interface AppGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun build(@Provides context: Context) : AppGraph
    }

    class Initializer : NoDependencyInitializer<Any> {
        override fun create(context: Context): Any = createGraphFactory<Factory>()
            .build(context)
            .also { appGraph = it }
    }
}

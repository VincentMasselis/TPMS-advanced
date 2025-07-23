package com.masselis.tpmsadvanced.core.database.ioc

import com.masselis.tpmsadvanced.core.common.CoreCommonComponent
import com.masselis.tpmsadvanced.core.database.SQLiteOpenHelperUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

public interface CoreDatabaseComponent {
    public val sQLiteOpenHelperUseCase: SQLiteOpenHelperUseCase

    public companion object : CoreDatabaseComponent by InternalComponent
}

@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalComponent : CoreDatabaseComponent {
    @DependencyGraph.Factory
    interface Factory {
        fun build(@Includes coreCommonComponent: CoreCommonComponent): InternalComponent
    }

    companion object : InternalComponent by createGraphFactory<Factory>().build(CoreCommonComponent)
}

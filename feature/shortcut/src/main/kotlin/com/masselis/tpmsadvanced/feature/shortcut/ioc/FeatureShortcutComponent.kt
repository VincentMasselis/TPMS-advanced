package com.masselis.tpmsadvanced.feature.shortcut.ioc

import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import com.masselis.tpmsadvanced.feature.shortcut.usecase.ShortcutUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

@Suppress("unused")
@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class]
)
internal interface FeatureShortcutComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes featureMainComponent: FeatureMainComponent,
        ): FeatureShortcutComponent
    }

    val shortcutUseCase: ShortcutUseCase

    companion object : FeatureShortcutComponent by createGraphFactory<Factory>()
        .build(FeatureMainComponent) {

        init {
            // Initializes it
            shortcutUseCase
        }
    }
}

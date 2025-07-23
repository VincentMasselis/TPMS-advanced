package com.masselis.tpmsadvanced.feature.shortcut.usecase

import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainGraph
import com.masselis.tpmsadvanced.feature.shortcut.ioc.ShortcutUseCase
import dagger.Component

@Component(
    dependencies = [
        FeatureMainGraph::class
    ]
)
@FeatureShortcutComponent.Scope
internal interface FeatureShortcutComponent {

    @javax.inject.Scope
    annotation class Scope

    val shortcutUseCase: ShortcutUseCase

    companion object : FeatureShortcutComponent by DaggerFeatureShortcutComponent
        .builder()
        .featureCoreComponent(FeatureMainGraph)
        .build() {
        init {
            // Initialize it
            shortcutUseCase
        }
    }
}

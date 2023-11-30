package com.masselis.tpmsadvanced.feature.shortcut.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.feature.shortcut.ioc.ShortcutUseCase
import dagger.Component

@Component(
    dependencies = [
        FeatureCoreComponent::class
    ]
)
@FeatureShortcutComponent.Scope
internal interface FeatureShortcutComponent {

    @javax.inject.Scope
    annotation class Scope

    val shortcutUseCase: ShortcutUseCase

    companion object : FeatureShortcutComponent by DaggerFeatureShortcutComponent
        .builder()
        .featureCoreComponent(FeatureCoreComponent)
        .build() {
        init {
            // Initialize it
            shortcutUseCase
        }
    }
}

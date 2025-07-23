package com.masselis.tpmsadvanced.feature.shortcut.usecase

import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import com.masselis.tpmsadvanced.feature.shortcut.ioc.ShortcutUseCase
import dagger.Component

@Component(
    dependencies = [
        FeatureMainComponent::class
    ]
)
@FeatureShortcutComponent.Scope
internal interface FeatureShortcutComponent {

    @javax.inject.Scope
    annotation class Scope

    val shortcutUseCase: ShortcutUseCase

    companion object : FeatureShortcutComponent by DaggerFeatureShortcutComponent
        .builder()
        .featureMainComponent(FeatureMainComponent)
        .build() {
        init {
            // Initialize it
            shortcutUseCase
        }
    }
}

package com.masselis.tpmsadvanced.feature.shortcut.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.feature.shortcut.ioc.ShortcutUseCase
import dagger.Component
import javax.inject.Inject

@Component(
    dependencies = [
        FeatureCoreComponent::class
    ]
)
@FeatureShortcutComponent.Scope
internal interface FeatureShortcutComponent {

    @Component.Factory
    interface Factory {
        fun build(featureCoreComponent: FeatureCoreComponent = FeatureCoreComponent): FeatureShortcutComponent
    }

    @javax.inject.Scope
    annotation class Scope

    fun inject(injectable: Injectable)

    companion object : Injectable()

    abstract class Injectable : FeatureShortcutComponent by DaggerFeatureShortcutComponent
        .factory()
        .build() {

        @Inject
        lateinit var shortcutUseCase: ShortcutUseCase

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }
}

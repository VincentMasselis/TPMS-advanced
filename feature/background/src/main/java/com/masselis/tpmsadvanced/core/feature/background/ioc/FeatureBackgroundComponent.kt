package com.masselis.tpmsadvanced.core.feature.background.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import dagger.Component

@FeatureBackgroundComponent.Scope
@Component(
    dependencies = [
        DataUnitComponent::class
    ]
)
public interface FeatureBackgroundComponent {
    @Component.Factory
    public interface Factory {
        public fun build(dataUnitComponent: DataUnitComponent = DataUnitComponent): FeatureBackgroundComponent
    }

    @javax.inject.Scope
    public annotation class Scope

    public fun inject(injectable: Injectable)

    public companion object : Injectable()

    public abstract class Injectable protected constructor() :
        FeatureBackgroundComponent by DaggerFeatureBackgroundComponent
            .factory()
            .build() {

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

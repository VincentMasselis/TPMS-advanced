package com.masselis.tpmsadvanced.core.feature.unit.ioc

import com.masselis.tpmsadvanced.core.feature.unit.interfaces.UnitsViewModel
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import dagger.Component
import javax.inject.Inject
import javax.inject.Provider

@FeatureUnitComponent.Scope
@Component(
    dependencies = [
        DataUnitComponent::class
    ]
)
public interface FeatureUnitComponent {
    @Component.Factory
    public abstract class Factory {
        internal abstract fun build(dataUnitComponent: DataUnitComponent): FeatureUnitComponent
    }

    @javax.inject.Scope
    public annotation class Scope

    public fun inject(injectable: Injectable)

    public companion object : Injectable()

    @Suppress("PropertyName", "VariableNaming")
    public abstract class Injectable protected constructor() :
        FeatureUnitComponent by DaggerFeatureUnitComponent
            .factory()
            .build(DataUnitComponent) {

        @Inject
        internal lateinit var UnitsViewModel: Provider<UnitsViewModel>

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

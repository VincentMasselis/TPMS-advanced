package com.masselis.tpmsadvanced.core.feature.unit.ioc

import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitComponent
import com.masselis.tpmsadvanced.core.feature.unit.interfaces.UnitsViewModel
import dagger.Component
import javax.inject.Inject

@SingleInstance
@Component(
    dependencies = [
        DataUnitComponent::class
    ]
)
public interface FeatureUnitComponent {
    @Component.Factory
    public interface Factory {
        public fun build(dataUnitComponent: DataUnitComponent = DataUnitComponent): FeatureUnitComponent
    }

    public fun inject(injectable: Injectable)

    public companion object : Injectable()

    public abstract class Injectable protected constructor() :
        FeatureUnitComponent by DaggerFeatureUnitComponent
            .factory()
            .build() {

        @Inject
        internal lateinit var unitsViewModel: UnitsViewModel

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }

}

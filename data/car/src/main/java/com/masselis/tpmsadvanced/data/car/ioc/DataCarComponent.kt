package com.masselis.tpmsadvanced.data.car.ioc

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import dagger.Component

@SingleInstance
@Component(
    modules = [Module::class]
)
public abstract class DataCarComponent {
    @Component.Factory
    internal abstract class Factory {
        internal abstract fun build(): DataCarComponent
    }

    internal abstract val debugFactory: DebugComponent.Factory

    public abstract val car: CarDatabase
}

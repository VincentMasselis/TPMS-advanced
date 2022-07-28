package com.masselis.tpmsadvanced.core.ioc

import com.masselis.tpmsadvanced.core.model.TyreLocation.*
import dagger.Module
import dagger.Provides

@Module(
    subcomponents = [
        TyreComponent::class
    ]
)
internal object TyreComponentFactoryModule {
    @Provides
    @CoreSingleton
    @Qualifier(FRONT_LEFT)
    fun frontLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_LEFT)

    @Provides
    @CoreSingleton
    @Qualifier(FRONT_RIGHT)
    fun frontRightTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_RIGHT)

    @Provides
    @CoreSingleton
    @Qualifier(REAR_LEFT)
    fun rearLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_LEFT)

    @Provides
    @CoreSingleton
    @Qualifier(REAR_RIGHT)
    fun rearRightTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_RIGHT)
}
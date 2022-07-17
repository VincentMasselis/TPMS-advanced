package com.masselis.tpmsadvanced.core.ioc

import com.masselis.tpmsadvanced.core.model.TyreLocation.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    subcomponents = [
        TyreComponent::class
    ]
)
internal object TyreComponentFactoryModule {
    @Provides
    @Singleton
    @Qualifier(FRONT_LEFT)
    fun frontLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_LEFT)

    @Provides
    @Singleton
    @Qualifier(FRONT_RIGHT)
    fun frontRightTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_RIGHT)

    @Provides
    @Singleton
    @Qualifier(REAR_LEFT)
    fun rearLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_LEFT)

    @Provides
    @Singleton
    @Qualifier(REAR_RIGHT)
    fun rearRightTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_RIGHT)
}
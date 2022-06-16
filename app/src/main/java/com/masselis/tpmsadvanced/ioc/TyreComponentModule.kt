package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.model.TyreLocation
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    subcomponents = [
        TyreComponent::class
    ]
)
object TyreComponentModule {
    @Provides
    @Singleton
    @TyreLocation.Qualifier(TyreLocation.FRONT_LEFT)
    fun frontLeftTyreComponent(factory: TyreComponent.Factory) =
        factory.build(TyreLocation.FRONT_LEFT)

    @Provides
    @Singleton
    @TyreLocation.Qualifier(TyreLocation.FRONT_RIGHT)
    fun frontRightTyreComponent(factory: TyreComponent.Factory) =
        factory.build(TyreLocation.FRONT_RIGHT)

    @Provides
    @Singleton
    @TyreLocation.Qualifier(TyreLocation.REAR_LEFT)
    fun rearLeftTyreComponent(factory: TyreComponent.Factory) =
        factory.build(TyreLocation.REAR_LEFT)

    @Provides
    @Singleton
    @TyreLocation.Qualifier(TyreLocation.REAR_RIGHT)
    fun rearRightTyreComponent(factory: TyreComponent.Factory) =
        factory.build(TyreLocation.REAR_RIGHT)
}
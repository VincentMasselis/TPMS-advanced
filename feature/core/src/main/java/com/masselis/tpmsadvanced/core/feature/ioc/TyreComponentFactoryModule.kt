package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.TyreLocation.REAR_RIGHT
import dagger.Module
import dagger.Provides

@Module(
    subcomponents = [
        TyreComponent::class
    ]
)
internal object TyreComponentFactoryModule {
    @Provides
    @SingleInstance
    @TyreLocationQualifier(FRONT_LEFT)
    fun frontLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_LEFT)

    @Provides
    @SingleInstance
    @TyreLocationQualifier(FRONT_RIGHT)
    fun frontRightTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_RIGHT)

    @Provides
    @SingleInstance
    @TyreLocationQualifier(REAR_LEFT)
    fun rearLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_LEFT)

    @Provides
    @SingleInstance
    @TyreLocationQualifier(REAR_RIGHT)
    fun rearRightTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_RIGHT)
}

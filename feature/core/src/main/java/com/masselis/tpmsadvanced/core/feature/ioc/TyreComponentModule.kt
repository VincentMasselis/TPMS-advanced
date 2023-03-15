package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_RIGHT
import dagger.Module
import dagger.Provides

@Module(
    subcomponents = [
        TyreComponent::class
    ]
)
internal object TyreComponentModule {
    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(FRONT_LEFT)
    fun frontLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_LEFT)

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(FRONT_RIGHT)
    fun frontRightTyreComponent(factory: TyreComponent.Factory) = factory.build(FRONT_RIGHT)

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(REAR_LEFT)
    fun rearLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_LEFT)

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(REAR_RIGHT)
    fun rearRightTyreComponent(factory: TyreComponent.Factory) = factory.build(REAR_RIGHT)
}

package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import dagger.Module
import dagger.Provides

@Module(
    subcomponents = [
        TyreComponent::class
    ]
)
internal object TyreSubComponentModule {
    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(FRONT_LEFT)
    fun frontLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(setOf(FRONT_LEFT))

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(FRONT_RIGHT)
    fun frontRightTyreComponent(factory: TyreComponent.Factory) = factory.build(setOf(FRONT_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(REAR_LEFT)
    fun rearLeftTyreComponent(factory: TyreComponent.Factory) = factory.build(setOf(REAR_LEFT))

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(REAR_RIGHT)
    fun rearRightTyreComponent(factory: TyreComponent.Factory) = factory.build(setOf(REAR_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreAxleQualifier(FRONT)
    fun frontTyreComponent(factory: TyreComponent.Factory) =
        factory.build(setOf(FRONT_LEFT, FRONT_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreAxleQualifier(REAR)
    fun rearTyreComponent(factory: TyreComponent.Factory) =
        factory.build(setOf(REAR_LEFT, REAR_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreSideQualifier(LEFT)
    fun leftTyreComponent(factory: TyreComponent.Factory) =
        factory.build(setOf(FRONT_LEFT, REAR_LEFT))

    @Provides
    @VehicleComponent.Scope
    @TyreSideQualifier(RIGHT)
    fun rightTyreComponent(factory: TyreComponent.Factory) =
        factory.build(setOf(FRONT_RIGHT, REAR_RIGHT))
}

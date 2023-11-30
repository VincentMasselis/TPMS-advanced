package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.Module
import dagger.Provides

@Module(
    subcomponents = [
        InternalTyreComponent::class
    ]
)
internal object TyreSubComponentModule {
    @Provides
    fun internalFindTyre(useCase: FindTyreComponentUseCase): (Vehicle.Kind.Location) -> InternalTyreComponent =
        useCase

    @Provides
    fun findTyre(useCase: FindTyreComponentUseCase): (Vehicle.Kind.Location) -> TyreComponent =
        useCase

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(FRONT_LEFT)
    fun frontLeftTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(FRONT_LEFT))

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(FRONT_RIGHT)
    fun frontRightTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(FRONT_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(REAR_LEFT)
    fun rearLeftTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(REAR_LEFT))

    @Provides
    @VehicleComponent.Scope
    @TyreLocationQualifier(REAR_RIGHT)
    fun rearRightTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(REAR_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreAxleQualifier(FRONT)
    fun frontTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(FRONT_LEFT, FRONT_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreAxleQualifier(REAR)
    fun rearTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(REAR_LEFT, REAR_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @TyreSideQualifier(LEFT)
    fun leftTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(FRONT_LEFT, REAR_LEFT))

    @Provides
    @VehicleComponent.Scope
    @TyreSideQualifier(RIGHT)
    fun rightTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(setOf(FRONT_RIGHT, REAR_RIGHT))
}

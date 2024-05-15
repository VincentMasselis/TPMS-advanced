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
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import dagger.Module
import dagger.Provides

@Module(
    subcomponents = [
        InternalTyreComponent::class
    ]
)
internal object TyreSubcomponentModule {
    @Provides
    fun internalFindTyre(useCase: FindTyreComponentUseCase): (Location) -> InternalTyreComponent =
        useCase

    @Provides
    fun findTyre(useCase: FindTyreComponentUseCase): (Location) -> TyreComponent =
        useCase

    @Provides
    @VehicleComponent.Scope
    @WheelLocationQualifier(FRONT_LEFT)
    fun frontLeftTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Wheel(FRONT_LEFT))

    @Provides
    @VehicleComponent.Scope
    @WheelLocationQualifier(FRONT_RIGHT)
    fun frontRightTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Wheel(FRONT_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @WheelLocationQualifier(REAR_LEFT)
    fun rearLeftTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Wheel(REAR_LEFT))

    @Provides
    @VehicleComponent.Scope
    @WheelLocationQualifier(REAR_RIGHT)
    fun rearRightTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Wheel(REAR_RIGHT))

    @Provides
    @VehicleComponent.Scope
    @AxleQualifier(FRONT)
    fun frontTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Axle(FRONT))

    @Provides
    @VehicleComponent.Scope
    @AxleQualifier(REAR)
    fun rearTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Axle(REAR))

    @Provides
    @VehicleComponent.Scope
    @SideQualifier(LEFT)
    fun leftTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Side(LEFT))

    @Provides
    @VehicleComponent.Scope
    @SideQualifier(RIGHT)
    fun rightTyreComponent(factory: InternalTyreComponent.Factory) =
        factory.build(Location.Side(RIGHT))
}

package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.FindTyreComponentUseCase
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
internal interface TyreSubcomponentBindings {

    @Binds
    val FindTyreComponentUseCase.bind: (Location) -> TyreComponent

    @Binds
    val FindTyreComponentUseCase.internalBind: (Location) -> InternalTyreComponent

    companion object {

        @Provides
        @SingleIn(VehicleComponent::class)
        @WheelLocationQualifier(FRONT_LEFT)
        fun frontLeftTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Wheel(FRONT_LEFT))

        @Provides
        @SingleIn(VehicleComponent::class)
        @WheelLocationQualifier(FRONT_RIGHT)
        fun frontRightTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Wheel(FRONT_RIGHT))

        @Provides
        @SingleIn(VehicleComponent::class)
        @WheelLocationQualifier(REAR_LEFT)
        fun rearLeftTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Wheel(REAR_LEFT))

        @Provides
        @SingleIn(VehicleComponent::class)
        @WheelLocationQualifier(REAR_RIGHT)
        fun rearRightTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Wheel(REAR_RIGHT))

        @Provides
        @SingleIn(VehicleComponent::class)
        @AxleQualifier(FRONT)
        fun frontTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Axle(FRONT))

        @Provides
        @SingleIn(VehicleComponent::class)
        @AxleQualifier(REAR)
        fun rearTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Axle(REAR))

        @Provides
        @SingleIn(VehicleComponent::class)
        @SideQualifier(LEFT)
        fun leftTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Side(LEFT))

        @Provides
        @SingleIn(VehicleComponent::class)
        @SideQualifier(RIGHT)
        fun rightTyreComponent(factory: InternalTyreComponent.Factory): InternalTyreComponent =
            factory.build(Location.Side(RIGHT))
    }
}

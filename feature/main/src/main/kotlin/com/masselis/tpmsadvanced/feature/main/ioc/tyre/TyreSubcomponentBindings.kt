package com.masselis.tpmsadvanced.feature.main.ioc.tyre

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.FindTyreComponentUseCase
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@Suppress("unused")
@ContributesTo(VehicleComponent.Scope::class)
public interface TyreSubcomponentBindings {

    @Provides
    private fun findTyreComponentUseCase(
        vehicle: Vehicle,
        @WheelLocationQualifier(FRONT_LEFT) frontLeft: Lazy<TyreComponent>,
        @WheelLocationQualifier(FRONT_RIGHT) frontRight: Lazy<TyreComponent>,
        @WheelLocationQualifier(REAR_LEFT) rearLeft: Lazy<TyreComponent>,
        @WheelLocationQualifier(REAR_RIGHT) rearRight: Lazy<TyreComponent>,
        @AxleQualifier(FRONT) front: Lazy<TyreComponent>,
        @AxleQualifier(REAR) rear: Lazy<TyreComponent>,
        @SideQualifier(LEFT) left: Lazy<TyreComponent>,
        @SideQualifier(RIGHT) right: Lazy<TyreComponent>,
    ): FindTyreComponentUseCase = FindTyreComponentUseCase(
        vehicle = vehicle,
        frontLeft = frontLeft,
        frontRight = frontRight,
        rearLeft = rearLeft,
        rearRight = rearRight,
        front = front,
        rear = rear,
        left = left,
        right = right
    )

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @WheelLocationQualifier(FRONT_LEFT)
    private fun frontLeftTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Wheel(FRONT_LEFT))

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @WheelLocationQualifier(FRONT_RIGHT)
    private fun frontRightTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Wheel(FRONT_RIGHT))

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @WheelLocationQualifier(REAR_LEFT)
    private fun rearLeftTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Wheel(REAR_LEFT))

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @WheelLocationQualifier(REAR_RIGHT)
    private fun rearRightTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Wheel(REAR_RIGHT))

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @AxleQualifier(FRONT)
    private fun frontTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Axle(FRONT))

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @AxleQualifier(REAR)
    private fun rearTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Axle(REAR))

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @SideQualifier(LEFT)
    private fun leftTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Side(LEFT))

    @Provides
    @SingleIn(VehicleComponent.Scope::class)
    @SideQualifier(RIGHT)
    private fun rightTyreComponent(factory: TyreComponent.Factory): TyreComponent =
        factory.build(Location.Side(RIGHT))

    public val tyreSubcomponentInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val findTyreComponentUseCase: () -> FindTyreComponentUseCase,
    )

    public companion object {
        private val VehicleComponent.internal
            get() = (this as TyreSubcomponentBindings).tyreSubcomponentInternal

        internal val VehicleComponent.findTyreComponentUseCase
            get() = internal.findTyreComponentUseCase()
    }
}

package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreAxleQualifier
import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.TyreLocationQualifier
import com.masselis.tpmsadvanced.core.feature.ioc.TyreSideQualifier
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.Side.RIGHT
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Named

public class FindTyreComponentUseCase @Inject internal constructor(
    @Named("base") private val vehicle: Vehicle,
    @TyreLocationQualifier(FRONT_LEFT) private val frontLeft: Lazy<TyreComponent>,
    @TyreLocationQualifier(FRONT_RIGHT) private val frontRight: Lazy<TyreComponent>,
    @TyreLocationQualifier(REAR_LEFT) private val rearLeft: Lazy<TyreComponent>,
    @TyreLocationQualifier(REAR_RIGHT) private val rearRight: Lazy<TyreComponent>,
    @TyreAxleQualifier(FRONT) private val front: Lazy<TyreComponent>,
    @TyreAxleQualifier(REAR) private val rear: Lazy<TyreComponent>,
    @TyreSideQualifier(LEFT) private val left: Lazy<TyreComponent>,
    @TyreSideQualifier(RIGHT) private val right: Lazy<TyreComponent>,
) {
    internal fun find(location: SensorLocation): TyreComponent {
        when (vehicle.kind) {
            Vehicle.Kind.CAR -> {}
            Vehicle.Kind.SINGLE_AXLE_TRAILER -> error("Cannot request for a specific tyre location for a single axle trailer, use SensorLocation.Side instead")
            Vehicle.Kind.MOTORCYCLE -> error("Cannot request for a specific tyre location for a motorcycle, use SensorLocation.Axle instead")
            Vehicle.Kind.TADPOLE_THREE_WHEELER -> when (location) {
                FRONT_LEFT, FRONT_RIGHT -> {}
                REAR_LEFT, REAR_RIGHT -> error("Cannot request for a specific rear tyre location for a tadpole three wheeler, use SensorLocation.Axle.REAR instead")
            }
            Vehicle.Kind.DELTA_THREE_WHEELER -> when (location) {
                FRONT_LEFT, FRONT_RIGHT -> error("Cannot request for a specific front tyre location for a delta three wheeler, use SensorLocation.Axle.FRONT instead")
                REAR_LEFT, REAR_RIGHT -> {}
            }
        }
        return when (location) {
            FRONT_LEFT -> frontLeft
            FRONT_RIGHT -> frontRight
            REAR_LEFT -> rearLeft
            REAR_RIGHT -> rearRight
        }.get()
    }

    internal fun find(axle: SensorLocation.Axle): TyreComponent {
        when (vehicle.kind) {
            Vehicle.Kind.CAR -> error("Cannot request for an axle for a car, use a specific tyre location instead")
            Vehicle.Kind.SINGLE_AXLE_TRAILER -> error("Cannot request for an axle for a single axle trailer, use SensorLocation.Side instead")
            Vehicle.Kind.MOTORCYCLE -> {}
            Vehicle.Kind.TADPOLE_THREE_WHEELER -> when (axle) {
                FRONT -> error("Cannot request a front axle for a tadpole three wheeler, use a specific tyre location instead")
                REAR -> {}
            }
            Vehicle.Kind.DELTA_THREE_WHEELER -> when (axle) {
                FRONT -> {}
                REAR -> error("Cannot request a rear axle for a delta three wheeler, use a specific tyre location instead")
            }
        }
        return when (axle) {
            FRONT -> front
            REAR -> rear
        }.get()
    }

    internal fun find(side: SensorLocation.Side): TyreComponent {
        when (vehicle.kind) {
            Vehicle.Kind.CAR -> error("Cannot request a side for a car, use a specific tyre location instead")
            Vehicle.Kind.SINGLE_AXLE_TRAILER -> {}
            Vehicle.Kind.MOTORCYCLE -> error("Cannot request a side for a motorcycle, use a SensorLocation.Axle instead")
            Vehicle.Kind.TADPOLE_THREE_WHEELER -> error("Cannot request a side for a tadpole three wheeler, use a SensorLocation.Axle.REAR or TyreLocation.FRONT_* instead")
            Vehicle.Kind.DELTA_THREE_WHEELER -> error("Cannot request a side for a delta three wheeler, use a SensorLocation.Axle.FRONT or TyreLocation.REAR_* instead")
        }
        return when (side) {
            LEFT -> left
            RIGHT -> right
        }.get()
    }
}

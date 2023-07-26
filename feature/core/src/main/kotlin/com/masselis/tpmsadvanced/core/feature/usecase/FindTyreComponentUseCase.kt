package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreAxleQualifier
import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.TyreLocationQualifier
import com.masselis.tpmsadvanced.core.feature.ioc.TyreSideQualifier
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
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

@Suppress("LongParameterList")
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
) : (Vehicle.Kind.Location) -> TyreComponent {

    @Suppress("MaxLineLength")
    public fun find(location: Vehicle.Kind.Location): TyreComponent {
        assert(vehicle.kind.locations.contains(location)) {
            "Cannot get a TyreComponent for the filled location $location according to the vehicle kind ${vehicle.kind}"
        }
        return when (location) {
            is Vehicle.Kind.Location.Wheel -> when (location.location) {
                FRONT_LEFT -> frontLeft
                FRONT_RIGHT -> frontRight
                REAR_LEFT -> rearLeft
                REAR_RIGHT -> rearRight
            }

            is Vehicle.Kind.Location.Axle -> when (location.axle) {
                FRONT -> front
                REAR -> rear
            }

            is Vehicle.Kind.Location.Side -> when (location.side) {
                LEFT -> left
                RIGHT -> right
            }
        }.get()
    }

    override fun invoke(p1: Vehicle.Kind.Location): TyreComponent = find(p1)

}

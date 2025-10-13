package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.InternalTyreComponent

@Suppress("LongParameterList")
internal class FindTyreComponentUseCase(
    private val vehicle: Vehicle,
    private val frontLeft: Lazy<InternalTyreComponent>,
    private val frontRight: Lazy<InternalTyreComponent>,
    private val rearLeft: Lazy<InternalTyreComponent>,
    private val rearRight: Lazy<InternalTyreComponent>,
    private val front: Lazy<InternalTyreComponent>,
    private val rear: Lazy<InternalTyreComponent>,
    private val left: Lazy<InternalTyreComponent>,
    private val right: Lazy<InternalTyreComponent>,
) : (Vehicle.Kind.Location) -> InternalTyreComponent {
    override fun invoke(location: Vehicle.Kind.Location): InternalTyreComponent {
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
        }.value
    }
}

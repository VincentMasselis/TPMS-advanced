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
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreComponent

@Suppress("LongParameterList")
internal class FindTyreComponentUseCase(
    private val vehicle: Vehicle,
    private val frontLeft: Lazy<TyreComponent>,
    private val frontRight: Lazy<TyreComponent>,
    private val rearLeft: Lazy<TyreComponent>,
    private val rearRight: Lazy<TyreComponent>,
    private val front: Lazy<TyreComponent>,
    private val rear: Lazy<TyreComponent>,
    private val left: Lazy<TyreComponent>,
    private val right: Lazy<TyreComponent>,
) : (Vehicle.Kind.Location) -> TyreComponent {
    override fun invoke(location: Vehicle.Kind.Location): TyreComponent {
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

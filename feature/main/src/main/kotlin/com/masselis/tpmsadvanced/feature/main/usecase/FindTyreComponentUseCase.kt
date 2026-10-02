package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_RIGHT
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
) : (Location) -> TyreComponent {
    override fun invoke(location: Location): TyreComponent {
        assert(vehicle.kind.locations.contains(location)) {
            "Cannot get a TyreComponent for the filled location $location according to the vehicle kind ${vehicle.kind}"
        }
        return when (location) {
            is Location.Wheel -> when (location) {
                FRONT_LEFT -> frontLeft
                FRONT_RIGHT -> frontRight
                REAR_LEFT -> rearLeft
                REAR_RIGHT -> rearRight
            }

            is Location.Axle -> when (location) {
                FRONT -> front
                REAR -> rear
            }

            is Location.Side -> when (location) {
                LEFT -> left
                RIGHT -> right
            }
        }.value
    }
}

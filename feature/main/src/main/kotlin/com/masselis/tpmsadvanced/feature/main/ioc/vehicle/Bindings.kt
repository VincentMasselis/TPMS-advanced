package com.masselis.tpmsadvanced.feature.main.ioc.vehicle

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.DeleteVehicleViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.VehicleSettingsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.AxleQualifier
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.InternalTyreComponent
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.SideQualifier
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.WheelLocationQualifier
import com.masselis.tpmsadvanced.feature.main.usecase.ClearBoundSensorsUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleStateFlowUseCase
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused")
@BindingContainer
internal object Bindings {

    @SingleIn(VehicleComponent::class)
    @Provides
    private fun scope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @SingleIn(VehicleComponent::class)
    @Provides
    private fun vehicleRangesUseCase(
        vehicle: Vehicle,
        scope: CoroutineScope,
        database: VehicleDatabase
    ): VehicleRangesUseCase = VehicleRangesUseCase(vehicle, scope, database)

    @Provides
    private fun vehicleSettingsViewModelImpl(
        vehicleRangesUseCase: VehicleRangesUseCase,
        unitPreferences: UnitPreferences,
    ): VehicleSettingsViewModelImpl =
        VehicleSettingsViewModelImpl(vehicleRangesUseCase, unitPreferences)

    @Provides
    private fun deleteVehicleViewModelImpl(
        deleteVehicleUseCase: DeleteVehicleUseCase,
        vehicleStateFlowUseCase: VehicleStateFlowUseCase,
        vehicleCountStateFlowUseCase: VehicleCountStateFlowUseCase,
    ): DeleteVehicleViewModelImpl = DeleteVehicleViewModelImpl(
        deleteVehicleUseCase,
        vehicleStateFlowUseCase,
        vehicleCountStateFlowUseCase
    )

    @Provides
    @SingleIn(VehicleComponent::class)
    private fun vehicleStateFlowUseCase(
        vehicle: Vehicle,
        database: VehicleDatabase,
        scope: CoroutineScope
    ): VehicleStateFlowUseCase = VehicleStateFlowUseCase(vehicle, database, scope)

    @Provides
    private fun stateFlowVehicle(uc: VehicleStateFlowUseCase): StateFlow<Vehicle> = uc

    @Provides
    private fun findTyreComponentUseCase(
        vehicle: Vehicle,
        @WheelLocationQualifier(FRONT_LEFT) frontLeft: Lazy<InternalTyreComponent>,
        @WheelLocationQualifier(FRONT_RIGHT) frontRight: Lazy<InternalTyreComponent>,
        @WheelLocationQualifier(REAR_LEFT) rearLeft: Lazy<InternalTyreComponent>,
        @WheelLocationQualifier(REAR_RIGHT) rearRight: Lazy<InternalTyreComponent>,
        @AxleQualifier(FRONT) front: Lazy<InternalTyreComponent>,
        @AxleQualifier(REAR) rear: Lazy<InternalTyreComponent>,
        @SideQualifier(LEFT) left: Lazy<InternalTyreComponent>,
        @SideQualifier(RIGHT) right: Lazy<InternalTyreComponent>,
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
    private fun deleteVehicleUseCase(
        vehicle: Vehicle,
        currentVehicleUseCase: CurrentVehicleUseCase,
        database: VehicleDatabase,
        scope: CoroutineScope,
    ): DeleteVehicleUseCase = DeleteVehicleUseCase(vehicle, currentVehicleUseCase, database, scope)

    @Provides
    private fun clearBoundSensorsUseCase(
        vehicle: Vehicle,
        sensorDatabase: SensorDatabase,
    ): ClearBoundSensorsUseCase = ClearBoundSensorsUseCase(vehicle, sensorDatabase)
}

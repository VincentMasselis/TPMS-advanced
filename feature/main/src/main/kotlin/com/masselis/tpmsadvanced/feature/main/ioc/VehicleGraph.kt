package com.masselis.tpmsadvanced.feature.main.ioc

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.unit.ioc.DataUnitGraph
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
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
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.DeleteVehicleViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.VehicleSettingsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.ClearBoundSensorsUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.FindTyreGraphUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleStateFlowUseCase
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

@Suppress("VariableNaming")
public interface VehicleGraph {

    public sealed interface Scope

    @Named("base")
    public val vehicle: Vehicle
    public val vehicleStateFlow: StateFlow<Vehicle>

    public companion object : (Vehicle) -> VehicleGraph by FeatureMainGraph
        .vehicleGraphCacheUseCase
}

@Suppress("PropertyName", "FunctionName", "VariableNaming", "unused")
@DependencyGraph(VehicleGraph.Scope::class, isExtendable = true)
internal interface InternalVehicleGraph : VehicleGraph {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes featureMainGraph: FeatureMainGraph,
            @Includes dataVehicleComponent: DataVehicleComponent,
            @Includes dataUnitGraph: DataUnitGraph,
            @Provides @Named("base") vehicle: Vehicle
        ): InternalVehicleGraph
    }

    val ClearBoundSensorsViewModel: ClearBoundSensorsViewModelImpl.Factory
    fun VehicleSettingsViewModel(): VehicleSettingsViewModelImpl
    fun DeleteVehicleViewModel(): DeleteVehicleViewModelImpl

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    private fun scope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Binds
    private fun stateFlowVehicle(uc: VehicleStateFlowUseCase): StateFlow<Vehicle> = uc

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    private fun vehicleRangesUseCase(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope,
        database: VehicleDatabase,
    ): VehicleRangesUseCase = VehicleRangesUseCase(vehicle, scope, database)

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    private fun vehicleStateFlowUseCase(
        @Named("base") vehicle: Vehicle,
        database: VehicleDatabase,
        scope: CoroutineScope
    ): VehicleStateFlowUseCase = VehicleStateFlowUseCase(vehicle, database, scope)

    @Provides
    private fun vehicleCountStateFlowUseCase(
        vehicleDatabase: VehicleDatabase
    ): VehicleCountStateFlowUseCase = VehicleCountStateFlowUseCase(vehicleDatabase)

    @Provides
    private fun clearBoundSensorsUseCase(
        @Named("base") vehicle: Vehicle,
        sensorDatabase: SensorDatabase
    ): ClearBoundSensorsUseCase = ClearBoundSensorsUseCase(vehicle, sensorDatabase)

    @Provides
    private fun deleteVehicleUseCase(
        @Named("base") vehicle: Vehicle,
        currentVehicleUseCase: CurrentVehicleUseCase,
        database: VehicleDatabase,
        scope: CoroutineScope
    ): DeleteVehicleUseCase = DeleteVehicleUseCase(vehicle, currentVehicleUseCase, database, scope)

    @Provides
    private fun vehicleSettingsViewModelImpl(
        vehicleRangesUseCase: VehicleRangesUseCase,
        unitPreferences: UnitPreferences,
    ): VehicleSettingsViewModelImpl = VehicleSettingsViewModelImpl(
        vehicleRangesUseCase,
        unitPreferences
    )

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
    private fun findTyreComponentUseCase(
        @Named("base") vehicle: Vehicle,
        @WheelLocationQualifier(FRONT_LEFT) frontLeft: Lazy<InternalTyreGraph>,
        @WheelLocationQualifier(FRONT_RIGHT) frontRight: Lazy<InternalTyreGraph>,
        @WheelLocationQualifier(REAR_LEFT) rearLeft: Lazy<InternalTyreGraph>,
        @WheelLocationQualifier(REAR_RIGHT) rearRight: Lazy<InternalTyreGraph>,
        @AxleQualifier(FRONT) front: Lazy<InternalTyreGraph>,
        @AxleQualifier(REAR) rear: Lazy<InternalTyreGraph>,
        @SideQualifier(LEFT) left: Lazy<InternalTyreGraph>,
        @SideQualifier(RIGHT) right: Lazy<InternalTyreGraph>,
    ): FindTyreGraphUseCase = FindTyreGraphUseCase(
        vehicle,
        frontLeft,
        frontRight,
        rearLeft,
        rearRight,
        front,
        rear,
        left,
        right
    )

    @Binds
    private fun internalIFindTyre(
        useCase: FindTyreGraphUseCase
    ): (Location) -> InternalTyreGraph = useCase

    val internalTyreGraph: (Location) -> InternalTyreGraph

    @Binds
    private fun findTyre(useCase: FindTyreGraphUseCase): (Location) -> TyreGraph = useCase

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @WheelLocationQualifier(FRONT_LEFT)
    private fun frontLeftTyreComponent(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Wheel(FRONT_LEFT),
        scope
    )

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @WheelLocationQualifier(FRONT_RIGHT)
    private fun frontRightTyreGraph(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Wheel(FRONT_RIGHT),
        scope
    )

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @WheelLocationQualifier(REAR_LEFT)
    private fun rearLeftTyreGraph(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Wheel(REAR_LEFT),
        scope
    )

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @WheelLocationQualifier(REAR_RIGHT)
    private fun rearRightTyreGraph(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Wheel(REAR_RIGHT),
        scope
    )

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @AxleQualifier(FRONT)
    private fun frontTyreGraph(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Axle(FRONT),
        scope
    )

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @AxleQualifier(REAR)
    private fun rearTyreGraph(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Axle(REAR),
        scope
    )

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @SideQualifier(LEFT)
    private fun leftTyreGraph(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Side(LEFT),
        scope
    )

    @Provides
    @SingleIn(VehicleGraph.Scope::class)
    @SideQualifier(RIGHT)
    private fun rightTyreGraph(
        @Named("base") vehicle: Vehicle,
        scope: CoroutineScope
    ): InternalTyreGraph = InternalTyreGraph.Factory.build(
        this,
        vehicle,
        Location.Side(RIGHT),
        scope
    )
}

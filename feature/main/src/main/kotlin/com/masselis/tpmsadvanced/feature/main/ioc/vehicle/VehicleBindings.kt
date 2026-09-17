package com.masselis.tpmsadvanced.feature.main.ioc.vehicle

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.DeleteVehicleViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.VehicleSettingsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.ClearBoundSensorsUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleStateFlowUseCase
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused", "FunctionNaming")
@ContributesTo(VehicleComponent.Scope::class)
public interface VehicleBindings {

    @SingleIn(VehicleComponent.Scope::class)
    @Provides
    @VehicleLifecycle
    private fun scope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @SingleIn(VehicleComponent.Scope::class)
    @Provides
    private fun vehicleRangesUseCase(
        vehicle: Vehicle,
        @VehicleLifecycle scope: CoroutineScope,
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
    @SingleIn(VehicleComponent.Scope::class)
    private fun vehicleStateFlowUseCase(
        vehicle: Vehicle,
        database: VehicleDatabase,
        @VehicleLifecycle scope: CoroutineScope
    ): VehicleStateFlowUseCase = VehicleStateFlowUseCase(vehicle, database, scope)

    @Provides
    private fun stateFlowVehicle(uc: VehicleStateFlowUseCase): StateFlow<Vehicle> = uc

    @Provides
    private fun deleteVehicleUseCase(
        vehicle: Vehicle,
        currentVehicleUseCase: CurrentVehicleUseCase,
        database: VehicleDatabase,
        @VehicleLifecycle scope: CoroutineScope,
    ): DeleteVehicleUseCase = DeleteVehicleUseCase(vehicle, currentVehicleUseCase, database, scope)

    @Provides
    private fun clearBoundSensorsUseCase(
        vehicle: Vehicle,
        sensorDatabase: SensorDatabase,
    ): ClearBoundSensorsUseCase = ClearBoundSensorsUseCase(vehicle, sensorDatabase)

    public val internal: Internal

    @Inject
    public class Internal internal constructor(
        internal val clearBoundSensorsViewModel: ClearBoundSensorsViewModelImpl.Factory,
        internal val vehicleSettingsViewModel: () -> VehicleSettingsViewModelImpl,
        internal val deleteVehicleViewModel: () -> DeleteVehicleViewModelImpl,
    )

    public companion object {
        private val VehicleComponent.internal
            get() = (this as VehicleBindings).internal

        internal val VehicleComponent.ClearBoundSensorsViewModel
            get() = internal.clearBoundSensorsViewModel

        internal fun VehicleComponent.VehicleSettingsViewModel() =
            internal.vehicleSettingsViewModel()

        internal fun VehicleComponent.DeleteVehicleViewModel() = internal.deleteVehicleViewModel()
    }
}

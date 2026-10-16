package com.masselis.tpmsadvanced.feature.main.ioc.vehicle

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.DeleteVehicleViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.SwapFrontRearViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.VehicleSettingsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.usecase.ClearBoundSensorsUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.DeleteVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.SwapFrontRearUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleCountStateFlowUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleStateFlowUseCase
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlin.reflect.KClass
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
    ): VehicleRangesUseCase = VehicleRangesUseCase(vehicle, database, scope)

    // Not a `MetroViewModelFactory` binding since the app graph already owns that type
    @Suppress("MaxLineLength")
    @SingleIn(VehicleComponent.Scope::class)
    @Provides
    @ForScope(VehicleComponent.Scope::class)
    private fun viewModelFactory(
        viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>,
        assistedFactoryProviders: Map<KClass<out ViewModel>, () -> ViewModelAssistedFactory>,
        manualAssistedFactoryProviders: Map<KClass<out ManualViewModelAssistedFactory>, () -> ManualViewModelAssistedFactory>,
    ): MetroViewModelFactory = object : MetroViewModelFactory() {
        override val viewModelProviders = viewModelProviders
        override val assistedFactoryProviders = assistedFactoryProviders
        override val manualAssistedFactoryProviders = manualAssistedFactoryProviders
    }

    @Provides
    @IntoMap
    @ViewModelKey(VehicleSettingsViewModelImpl::class)
    private fun vehicleSettingsViewModel(
        vehicleRangesUseCase: VehicleRangesUseCase,
        unitPreferences: UnitPreferences,
    ): ViewModel = VehicleSettingsViewModelImpl(vehicleRangesUseCase, unitPreferences)

    @Provides
    @IntoMap
    @ViewModelAssistedFactoryKey(ClearBoundSensorsViewModelImpl::class)
    private fun clearBoundSensorsViewModelFactory(
        factory: ClearBoundSensorsViewModelImpl.Factory,
    ): ViewModelAssistedFactory = factory

    @Provides
    private fun swapRearFrontUseCase(
        vehicle: Vehicle,
        vehicleDatabase: VehicleDatabase,
        sensorDatabase: SensorDatabase,
    ): SwapFrontRearUseCase = SwapFrontRearUseCase(vehicle, vehicleDatabase, sensorDatabase)

    @Provides
    private fun swapRearFrontViewModelImpl(
        swapFrontRearUseCase: SwapFrontRearUseCase,
    ): SwapFrontRearViewModelImpl = SwapFrontRearViewModelImpl(swapFrontRearUseCase)

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
        internal val swapFrontRearViewModel: () -> SwapFrontRearViewModelImpl,
        internal val deleteVehicleViewModel: () -> DeleteVehicleViewModelImpl,
    )

    public companion object {
        private val VehicleComponent.internal
            get() = (this as VehicleBindings).internal

        context(comp: VehicleComponent)
        internal fun SwapFrontRearViewModel() = comp.internal.swapFrontRearViewModel()

        context(comp: VehicleComponent)
        internal fun DeleteVehicleViewModel() = comp.internal.deleteVehicleViewModel()
    }
}

package com.masselis.tpmsadvanced.feature.main.ioc.vehicle

import com.masselis.tpmsadvanced.core.ui.Keyed
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.DeleteVehicleViewModelImpl
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl.VehicleSettingsViewModelImpl
import com.masselis.tpmsadvanced.feature.main.ioc.InternalComponent
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.InternalTyreComponent
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreComponent
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreSubcomponentBindings
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.flow.StateFlow

@Suppress("PropertyName", "VariableNaming")
public sealed interface VehicleComponent {

    public val vehicle: Vehicle
    public val vehicleStateFlow: StateFlow<Vehicle>

    public val vehicleRangesUseCase: VehicleRangesUseCase

    public val TyreComponent: (Vehicle.Kind.Location) -> TyreComponent
    public val TyreComponents: List<TyreComponent> get() = vehicle.kind.locations.map(TyreComponent)

    public companion object : (Vehicle) -> VehicleComponent by InternalVehicleComponent {
        public fun VehicleComponent.key(): Keyed = mapOf("vehicle_id" to vehicle.uuid.toString())
    }
}

@Suppress("PropertyName", "FunctionName", "VariableNaming", "unused")
@GraphExtension(
    scope = VehicleComponent::class,
    bindingContainers = [Bindings::class, TyreSubcomponentBindings::class]
)
internal interface InternalVehicleComponent : VehicleComponent {

    @GraphExtension.Factory
    interface Factory {
        fun build(@Provides vehicle: Vehicle): InternalVehicleComponent
    }

    override val TyreComponent: (Vehicle.Kind.Location) -> InternalTyreComponent

    val tyreFactory: InternalTyreComponent.Factory

    val ClearBoundSensorsViewModel: ClearBoundSensorsViewModelImpl.Factory
    fun VehicleSettingsViewModel(): VehicleSettingsViewModelImpl
    fun DeleteVehicleViewModel(): DeleteVehicleViewModelImpl

    companion object :
            (Vehicle) -> InternalVehicleComponent by InternalComponent.VehicleComponentFactory
}

package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.ClearBoundSensorsViewModelImpl
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.DeleteVehicleViewModelImpl
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.impl.VehicleSettingsViewModelImpl
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Named

@Suppress("PropertyName", "VariableNaming")
public interface VehicleComponent {

    @javax.inject.Scope
    public annotation class Scope

    @get:Named("base")
    public val vehicle: Vehicle
    public val vehicleStateFlow: StateFlow<Vehicle>

    public val vehicleRangesUseCase: VehicleRangesUseCase

    public val TyreComponent: (@JvmSuppressWildcards Vehicle.Kind.Location) -> @JvmSuppressWildcards TyreComponent

    public companion object : (Vehicle) -> VehicleComponent by InternalVehicleComponent
}

@Suppress("PropertyName", "FunctionName")
@VehicleComponent.Scope
@Subcomponent(
    modules = [VehicleModule::class, TyreSubcomponentModule::class]
)
internal interface InternalVehicleComponent : VehicleComponent {

    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance @Named("base") vehicle: Vehicle): InternalVehicleComponent
    }

    @Suppress("VariableNaming", "MaxLineLength")
    val InternalTyreComponent: (@JvmSuppressWildcards Vehicle.Kind.Location) -> @JvmSuppressWildcards InternalTyreComponent

    @Suppress("VariableNaming")
    val ClearBoundSensorsViewModel: ClearBoundSensorsViewModelImpl.Factory
    fun VehicleSettingsViewModel(): VehicleSettingsViewModelImpl
    fun DeleteVehicleViewModel(): DeleteVehicleViewModelImpl

    companion object : (Vehicle) -> InternalVehicleComponent by InternalComponent
        .vehicleComponentCacheUseCase
}

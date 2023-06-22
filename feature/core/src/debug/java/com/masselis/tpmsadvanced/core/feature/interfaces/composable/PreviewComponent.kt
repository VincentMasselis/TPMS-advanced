package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.lifecycle.SavedStateHandle
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorButtonViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteVehicleViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.VehicleSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.ioc.TyreComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.TyreAtmosphereUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration


internal class PreviewTyreComponent(
    override val tyreViewModelFactory: TyreViewModelImpl.Factory =
        object : TyreViewModelImpl.Factory {
            override fun build(
                savedStateHandle: SavedStateHandle,
                obsoleteTimeout: Duration
            ): TyreViewModelImpl = previewTyreViewModelImpl()
        },
    override val tyreStatViewModelFactory: TyreStatsViewModel.Factory =
        object : TyreStatsViewModel.Factory {
            override fun build(savedStateHandle: SavedStateHandle): TyreStatsViewModel =
                previewTyreStatViewModel()
        },
    override val bindSensorButtonViewModelFactory: BindSensorButtonViewModel.Factory =
        object : BindSensorButtonViewModel.Factory {
            override fun build(savedStateHandle: SavedStateHandle): BindSensorButtonViewModel =
                previewBindSensorViewModel()
        }
) : TyreComponent() {
    override val tyreAtmosphereUseCase: TyreAtmosphereUseCase
        get() = TODO("Not yet implemented")
}

@Suppress("LongParameterList")
internal class PreviewVehicleComponent(
    override val tyreComponent: FindTyreComponentUseCase =
        mockk<FindTyreComponentUseCase>().also {
            every { it.find(any()) } returns PreviewTyreComponent()
        },
    override val vehicle: Vehicle = previewVehicle,
    override val carFlow: StateFlow<Vehicle> = MutableStateFlow(previewVehicle),
    override val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory =
        object : ClearBoundSensorsViewModel.Factory {
            override fun build(savedStateHandle: SavedStateHandle): ClearBoundSensorsViewModel =
                previewClearBoundSensorsViewModel()
        },
    override val vehicleSettingsViewModel: VehicleSettingsViewModel.Factory =
        object : VehicleSettingsViewModel.Factory {
            override fun build(): VehicleSettingsViewModel = previewVehicleSettingsViewModel()
        },
    override val deleteVehicleViewModel: DeleteVehicleViewModel.Factory =
        object : DeleteVehicleViewModel.Factory {
            override fun build(savedStateHandle: SavedStateHandle): DeleteVehicleViewModel =
                previewDeleteVehicleViewModel()
        }
) : VehicleComponent() {
    override val vehicleRangesUseCase: VehicleRangesUseCase
        get() = TODO("Not yet implemented")
}

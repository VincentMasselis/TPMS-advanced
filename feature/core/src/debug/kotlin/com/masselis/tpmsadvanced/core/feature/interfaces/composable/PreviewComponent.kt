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
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


internal class PreviewTyreComponent(
    override val TyreViewModel: TyreViewModelImpl.Factory =
        object : TyreViewModelImpl.Factory {
            override fun invoke(
                savedStateHandle: SavedStateHandle,
            ): TyreViewModelImpl = previewTyreViewModelImpl()
        },
    override val TyreStatViewModel: TyreStatsViewModel.Factory =
        object : TyreStatsViewModel.Factory {
            override fun invoke(savedStateHandle: SavedStateHandle): TyreStatsViewModel =
                previewTyreStatViewModel()
        },
    override val BindSensorButtonViewModel: BindSensorButtonViewModel.Factory =
        object : BindSensorButtonViewModel.Factory {
            override fun invoke(savedStateHandle: SavedStateHandle): BindSensorButtonViewModel =
                previewBindSensorViewModel()
        }
) : TyreComponent() {
    override val tyreAtmosphereUseCase: TyreAtmosphereUseCase
        get() = error("Not implemented")
}

@Suppress("LongParameterList")
internal class PreviewVehicleComponent(
    override val tyreComponent: FindTyreComponentUseCase =
        mockk<FindTyreComponentUseCase>().also {
            every { it.find(any()) } returns PreviewTyreComponent()
        },
    override val vehicle: Vehicle = previewVehicle,
    override val carFlow: StateFlow<Vehicle> = MutableStateFlow(previewVehicle),
    override val ClearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory =
        object : ClearBoundSensorsViewModel.Factory {
            override fun invoke(savedStateHandle: SavedStateHandle): ClearBoundSensorsViewModel =
                previewClearBoundSensorsViewModel()
        },
) : VehicleComponent() {
    override fun VehicleSettingsViewModel(): VehicleSettingsViewModel =
        previewVehicleSettingsViewModel()

    override fun DeleteVehicleViewModel(): DeleteVehicleViewModel =
        previewDeleteVehicleViewModel()

    override val vehicleRangesUseCase: VehicleRangesUseCase
        get() = error("Not implemented")
}

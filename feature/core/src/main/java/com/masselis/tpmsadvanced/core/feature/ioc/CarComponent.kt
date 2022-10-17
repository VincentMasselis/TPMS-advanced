package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.BindSensorDialogViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.CarSettingsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearBoundSensorsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteCarAlertViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.DeleteCarViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.model.Car
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.*

@CarScope
@Subcomponent(
    modules = [
        CarModule::class,
        TyreComponentModule::class
    ]
)
internal abstract class CarComponent {
    @Subcomponent.Factory
    internal interface Factory {
        fun build(@BindsInstance carId: UUID): CarComponent
    }

    protected abstract val findTyreComponentUseCase: FindTyreComponentUseCase
    internal fun tyreComponent(location: SensorLocation) = findTyreComponentUseCase.find(location)

    internal abstract val carId: UUID
    internal abstract val carFlow: Flow<Car>
    internal abstract val scope: CoroutineScope

    internal abstract val clearBoundSensorsViewModel: ClearBoundSensorsViewModel.Factory
    internal abstract val carSettingsViewModel: CarSettingsViewModel.Factory
    internal abstract val bindSensorDialogViewModelFactory: BindSensorDialogViewModel.Factory
    internal abstract val deleteCarViewModel: DeleteCarViewModel
    internal abstract val deleteCarAlertViewModel: DeleteCarAlertViewModel
}

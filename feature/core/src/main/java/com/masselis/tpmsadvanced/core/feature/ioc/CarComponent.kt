package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.ClearFavouritesViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.SettingsViewModel
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

    internal abstract val clearFavouritesViewModel: ClearFavouritesViewModel.Factory
    internal abstract val settingsViewModel: SettingsViewModel.Factory
}

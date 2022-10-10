package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.data.car.Car
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import dagger.BindsInstance
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineScope

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
        fun build(@BindsInstance car: Car): CarComponent
    }

    protected abstract val findTyreComponentUseCase: FindTyreComponentUseCase
    internal fun tyreComponent(location: TyreLocation) = findTyreComponentUseCase.find(location)

    internal abstract val car: Car
    internal abstract val scope: CoroutineScope
}

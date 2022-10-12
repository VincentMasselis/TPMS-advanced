package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.SensorFavouriteViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import dagger.BindsInstance
import dagger.Subcomponent

@TyreScope
@Subcomponent(
    modules = [TyreModule::class]
)
public abstract class TyreComponent {

    @Subcomponent.Factory
    internal interface Factory {
        fun build(@BindsInstance location: SensorLocation): TyreComponent
    }

    internal abstract val tyreViewModelFactory: TyreViewModelImpl.Factory
    internal abstract val tyreStatViewModelFactory: TyreStatsViewModel.Factory
    internal abstract val sensorFavouriteViewModelFactory: SensorFavouriteViewModel.Factory
}

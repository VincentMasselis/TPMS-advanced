package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.interfaces.featureCoreComponent
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.SensorFavouriteViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel.TyreViewModelImpl
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import dagger.BindsInstance
import dagger.Subcomponent

@TyreScope
@Subcomponent(
    modules = [
        TyreComponentModule::class,
    ],
)
public abstract class TyreComponent {

    @Subcomponent.Factory
    internal interface Factory {
        fun build(
            @BindsInstance location: TyreLocation
        ): TyreComponent
    }

    internal abstract val tyreViewModelFactory: TyreViewModelImpl.Factory
    internal abstract val tyreStatViewModelFactory: TyreStatsViewModel.Factory
    internal abstract val sensorFavouriteViewModelFactory: SensorFavouriteViewModel.Factory

    public companion object {
        public val TyreLocation.component: TyreComponent
            get() = featureCoreComponent.tyreComponent(this)
    }
}

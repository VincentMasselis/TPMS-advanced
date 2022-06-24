package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import com.masselis.tpmsadvanced.model.TyreLocation
import dagger.BindsInstance
import dagger.Subcomponent

@SingleInstance
@Subcomponent
interface TyreComponent {
    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance location: TyreLocation): TyreComponent
    }

    val tyreViewModelFactory: TyreViewModel.Factory
    val tyreStatViewModelFactory: TyreStatsViewModel.Factory
}
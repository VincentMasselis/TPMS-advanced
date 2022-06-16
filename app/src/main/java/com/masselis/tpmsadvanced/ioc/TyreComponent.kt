package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.interfaces.viewmodel.TyreViewModel
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
interface TyreComponent {
    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance location: TyreLocation): TyreComponent
    }

    val tyreViewModelFactory: TyreViewModel.Factory
}
package com.masselis.tpmsadvanced.interfaces.ioc

import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import dagger.Component

@AppPhoneComponent.Scope
@Component(
    dependencies = [DataAppComponent::class]
)
internal interface AppPhoneComponent {
    @Component.Factory
    interface Factory {
        fun build(dataAppComponent: DataAppComponent = DataAppComponent): AppPhoneComponent
    }

    @javax.inject.Scope
    annotation class Scope

    val homeViewModel: HomeViewModel.Factory

    companion object : AppPhoneComponent by DaggerAppPhoneComponent
        .factory()
        .build()
}

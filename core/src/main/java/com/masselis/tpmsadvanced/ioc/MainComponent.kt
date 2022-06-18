package com.masselis.tpmsadvanced.ioc

import android.content.Context
import com.masselis.tpmsadvanced.usecase.FindTyreComponentUseCase
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TyreComponentFactoryModule::class
    ]
)
interface MainComponent {

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context): MainComponent
    }

    val findTyreComponent: FindTyreComponentUseCase
}
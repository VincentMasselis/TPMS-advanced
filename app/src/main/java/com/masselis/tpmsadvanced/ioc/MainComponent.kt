package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.usecase.FindTyreComponentUseCase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TyreComponentModule::class
    ]
)
interface MainComponent {
    val findTyreComponent: FindTyreComponentUseCase
}
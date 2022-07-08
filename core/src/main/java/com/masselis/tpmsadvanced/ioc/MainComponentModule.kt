package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.model.TyreLocation
import dagger.Module
import dagger.Provides

@Module
object MainComponentModule {
    @Provides
    fun favouritesUseCases() = TyreLocation.values().map { it.component.favouriteSensorUseCase }
}
package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.FavouriteSensorUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.TyreUseCase
import dagger.Binds
import dagger.Module

@Module
internal interface TyreComponentModule {
    @Binds
    fun sensorByteUseCase(favouriteSensorUseCase: FavouriteSensorUseCase): TyreUseCase
}

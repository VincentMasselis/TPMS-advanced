package com.masselis.tpmsadvanced.core.ioc

import com.masselis.tpmsadvanced.core.usecase.FavouriteSensorUseCase
import com.masselis.tpmsadvanced.core.usecase.RecordUseCase
import dagger.Binds
import dagger.Module

@Module
interface TyreComponentModule {
    @Binds
    fun sensorByteUseCase(favouriteSensorUseCase: FavouriteSensorUseCase): RecordUseCase
}
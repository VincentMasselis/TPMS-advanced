package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.usecase.FavouriteSensorUseCase
import com.masselis.tpmsadvanced.usecase.RecordUseCase
import dagger.Binds
import dagger.Module

@Module
interface TyreComponentModule {
    @Binds
    fun sensorByteUseCase(favouriteSensorUseCase: FavouriteSensorUseCase): RecordUseCase
}
package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.CurrentTyreBoundSensorUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.TyreUseCase
import dagger.Module
import dagger.Provides

@Module
internal object TyreModule {
    @Provides
    fun tyreUseCase(currentTyreBoundSensorUseCase: CurrentTyreBoundSensorUseCase): TyreUseCase =
        currentTyreBoundSensorUseCase
}

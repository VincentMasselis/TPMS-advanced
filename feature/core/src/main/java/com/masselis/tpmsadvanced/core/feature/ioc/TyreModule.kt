package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.FavouriteSensorUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.TyreUseCase
import dagger.Module
import dagger.Provides

@Module
internal object TyreModule {
    @Provides
    fun tyreUseCase(favouriteSensorUseCase: FavouriteSensorUseCase): TyreUseCase =
        favouriteSensorUseCase
}

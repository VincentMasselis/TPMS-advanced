package com.masselis.tpmsadvanced.core.feature.ioc

import com.masselis.tpmsadvanced.core.feature.usecase.CarUseCase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

@Module
internal object CarModule {
    @CarScope
    @Provides
    fun scope(): CoroutineScope = CoroutineScope(EmptyCoroutineContext)

    @Provides
    fun carFlow(carUseCase: CarUseCase) = carUseCase.flow
}

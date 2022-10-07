package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import com.masselis.tpmsadvanced.core.feature.usecase.CurrentCarUseCase
import javax.inject.Inject

internal class CurrentCarViewModel @Inject constructor(
    currentCarUseCase: CurrentCarUseCase
) {
    val flow = currentCarUseCase.flow
}
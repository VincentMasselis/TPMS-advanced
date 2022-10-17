package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentCarUseCase
import javax.inject.Inject

internal class CurrentCarComponentViewModel @Inject constructor(
    currentCarUseCase: CurrentCarUseCase
) : ViewModel() {
    val stateFlow = currentCarUseCase.flow
}

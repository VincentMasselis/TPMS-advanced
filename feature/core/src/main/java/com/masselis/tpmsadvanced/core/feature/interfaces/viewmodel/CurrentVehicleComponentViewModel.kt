package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class CurrentVehicleComponentViewModel @AssistedInject constructor(
    currentVehicleUseCase: CurrentVehicleUseCase,
    @Suppress("UNUSED_PARAMETER") @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): CurrentVehicleComponentViewModel
    }

    val stateFlow = currentVehicleUseCase.flow
}

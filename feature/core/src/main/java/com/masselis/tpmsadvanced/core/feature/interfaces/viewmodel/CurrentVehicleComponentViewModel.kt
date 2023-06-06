package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CurrentVehicleUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.util.UUID

internal class CurrentVehicleComponentViewModel @AssistedInject constructor(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    @Suppress("UNUSED_PARAMETER") @Assisted savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): CurrentVehicleComponentViewModel
    }

    val stateFlow = currentVehicleUseCase.flow

    fun setCurrent(uuid: UUID) = viewModelScope.launch {
        currentVehicleUseCase.setAsCurrent(uuid)
    }
}

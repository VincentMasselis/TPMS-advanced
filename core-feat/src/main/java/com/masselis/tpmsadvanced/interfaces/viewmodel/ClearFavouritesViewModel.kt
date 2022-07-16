package com.masselis.tpmsadvanced.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.interfaces.viewmodel.ClearFavouritesViewModel.State.AlreadyCleared
import com.masselis.tpmsadvanced.interfaces.viewmodel.ClearFavouritesViewModel.State.ClearingPossible
import com.masselis.tpmsadvanced.tools.asMutableStateFlow
import com.masselis.tpmsadvanced.usecase.FavouriteSensorUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize

class ClearFavouritesViewModel @AssistedInject constructor(
    private val useCases: List<FavouriteSensorUseCase>,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): ClearFavouritesViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object ClearingPossible : State()

        @Parcelize
        object AlreadyCleared : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", AlreadyCleared)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        combine(useCases.map { it.savedId }) { ids -> ids.any { it != null } }
            .distinctUntilChanged()
            .map { hasFavourites -> if (hasFavourites) ClearingPossible else AlreadyCleared }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun clear() {
        useCases.forEach { it.savedId.value = null }
    }
}
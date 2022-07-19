package com.masselis.tpmsadvanced.core.interfaces.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.usecase.FavouriteSensorUseCase
import com.masselis.tpmsadvanced.uicommon.asMutableStateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalCoroutinesApi::class)
class SensorFavouriteViewModel @AssistedInject constructor(
    private val favouriteSensorUseCase: FavouriteSensorUseCase,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun build(savedStateHandle: SavedStateHandle): SensorFavouriteViewModel
    }

    sealed class State : Parcelable {
        @Parcelize
        object Empty : State()

        @Parcelize
        data class RequestBond(val id: Int) : State()
    }

    private val mutableStateFlow = savedStateHandle
        .getLiveData<State>("STATE", State.Empty)
        .asMutableStateFlow()
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        favouriteSensorUseCase.savedId
            .flatMapLatest { savedId ->
                if (savedId == null) favouriteSensorUseCase
                    .foundIds
                    .map { State.RequestBond(it) }
                else
                    flowOf(State.Empty)
            }
            .onEach { mutableStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun save() {
        val state = stateFlow.value
        if (state is State.RequestBond)
            favouriteSensorUseCase.savedId.value = state.id
    }
}
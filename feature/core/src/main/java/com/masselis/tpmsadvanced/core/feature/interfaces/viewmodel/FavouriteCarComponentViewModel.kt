package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.core.feature.usecase.FavouriteCarUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class FavouriteCarComponentViewModel @Inject constructor(
    favouriteCarUseCase: FavouriteCarUseCase
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Current(val component: CarComponent) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        favouriteCarUseCase.flow
            .onEach { mutableStateFlow.value = State.Current(it) }
            .launchIn(viewModelScope)
    }
}

package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.FavouriteCarUseCase
import com.masselis.tpmsadvanced.data.car.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class FavouriteCarViewModel @Inject constructor(
    private val favouriteCarUseCase: FavouriteCarUseCase,
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class CurrentCar(val car: Car) : State()
    }

    private val mutableStateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        favouriteCarUseCase.flow
            .onEach { mutableStateFlow.value = State.CurrentCar(it.car) }
            .launchIn(viewModelScope)
    }

    fun setFavourite(car: Car) = viewModelScope.launch {
        favouriteCarUseCase.setFavourite(car)
    }
}

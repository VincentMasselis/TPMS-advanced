package com.masselis.tpmsadvanced.core.feature.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.core.feature.usecase.CarListUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.FavouriteCarUseCase
import com.masselis.tpmsadvanced.data.car.Car
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CurrentCarViewModel @Inject constructor(
    private val favouriteCarUseCase: FavouriteCarUseCase,
    carListUseCase: CarListUseCase
) : ViewModel() {
    val flow = favouriteCarUseCase.flow

    val carListFlow = carListUseCase.carListFlow
        .map { list -> list.sortedBy { it.isFavourite } }

    fun setFavourite(car: Car) = viewModelScope.launch {
        favouriteCarUseCase.setFavourite(car)
    }
}
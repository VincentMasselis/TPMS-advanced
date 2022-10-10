package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.Car
import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@Reusable
internal class FavouriteCarUseCase @Inject constructor(
    private val database: CarDatabase,
) {
    val flow = database.currentFavouriteFlow()
        .stateIn(
            CoroutineScope(EmptyCoroutineContext),
            SharingStarted.WhileSubscribed(),
            database.currentFavourite()
        )

    suspend fun setFavourite(car: Car) = database.setAsFavourite(car.uuid, true)
}
package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

internal class CurrentCarUseCase @Inject constructor(
    database: CarDatabase,
) {
    val flow = database.currentFavouriteFlow()
        .stateIn(
            CoroutineScope(IO),
            SharingStarted.Lazily,
            database.currentFavourite()
        )
}
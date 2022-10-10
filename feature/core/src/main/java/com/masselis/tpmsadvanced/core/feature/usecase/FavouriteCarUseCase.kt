package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.core.feature.ioc.SingleInstance
import com.masselis.tpmsadvanced.data.car.Car
import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@SingleInstance
internal class FavouriteCarUseCase @Inject constructor(
    private val database: CarDatabase,
    private val factory: CarComponent.Factory
) {

    internal val flow = database.currentFavouriteFlow()
        .distinctUntilChanged()
        .scan(null as CarComponent?) { previous, current ->
            previous?.scope?.cancel()
            factory.build(current)
        }
        .filterNotNull()
        .shareIn(
            CoroutineScope(EmptyCoroutineContext),
            SharingStarted.Eagerly,
            replay = 1
        )

    internal suspend fun setFavourite(car: Car) = database.setAsFavourite(car.uuid, true)
}

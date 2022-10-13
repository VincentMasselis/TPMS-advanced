package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.core.feature.ioc.SingleInstance
import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import com.masselis.tpmsadvanced.data.car.model.Car
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@SingleInstance
internal class FavouriteCarUseCase @Inject constructor(
    private val database: CarDatabase,
    private val factory: CarComponent.Factory
) {

    internal val flow = database.currentFavouriteFlow()
        .map { it.uuid }
        .distinctUntilChanged()
        .scan(null as CarComponent?) { previous, uuid ->
            previous?.scope?.cancel()
            factory.build(uuid)
        }
        .filterNotNull()
        .shareIn(
            GlobalScope,
            SharingStarted.Eagerly,
            replay = 1
        )

    internal suspend fun setFavourite(car: Car) = database.setAsFavourite(car.uuid, true)
}

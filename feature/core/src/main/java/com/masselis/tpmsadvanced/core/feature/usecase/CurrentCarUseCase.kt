package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.observableStateFlow
import com.masselis.tpmsadvanced.core.feature.ioc.CarComponent
import com.masselis.tpmsadvanced.core.feature.ioc.SingleInstance
import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import com.masselis.tpmsadvanced.data.car.model.Car
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID.randomUUID
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@SingleInstance
internal class CurrentCarUseCase @Inject constructor(
    private val database: CarDatabase,
    private val factory: CarComponent.Factory
) {

    private val mutableStateFlow = observableStateFlow(
        factory.build(database.currentCar().uuid)
    ) { old, _ ->
        old.scope.cancel()
    }

    internal val flow = mutableStateFlow.asStateFlow()

    init {
        database.currentCarFlow()
            .map { it.uuid }
            .distinctUntilChanged()
            .filter { it != mutableStateFlow.value.carId }
            .onEach { mutableStateFlow.value = factory.build(it) }
            .launchIn(GlobalScope)
    }

    internal suspend fun setAsCurrent(car: Car) = database.setIsCurrent(car.uuid, true)

    internal suspend fun insertAsCurrent(carName: String) = database
        .insert(randomUUID(), carName, true)
}

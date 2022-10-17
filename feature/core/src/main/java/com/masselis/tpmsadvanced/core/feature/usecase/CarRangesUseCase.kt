package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.CarScope
import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@CarScope
internal class CarRangesUseCase @Inject constructor(
    private val carId: UUID,
    private val database: CarDatabase,
    scope: CoroutineScope
) {

    val lowPressure = MutableStateFlow(database.selectLowPressure(carId))
    val highPressure = MutableStateFlow(database.selectHighPressure(carId))
    val lowTemp = MutableStateFlow(database.selectLowTemp(carId))
    val normalTemp = MutableStateFlow(database.selectNormalTemp(carId))
    val highTemp = MutableStateFlow(database.selectHighTemp(carId))

    init {
        lowPressure
            .debounce(100.milliseconds)
            .onEach { database.updateLowPressure(it, carId) }
            .launchIn(scope)

        highPressure
            .debounce(100.milliseconds)
            .onEach { database.updateHighPressure(it, carId) }
            .launchIn(scope)

        lowTemp
            .debounce(100.milliseconds)
            .onEach { database.updateLowTemp(it, carId) }
            .launchIn(scope)

        normalTemp
            .debounce(100.milliseconds)
            .onEach { database.updateNormalTemp(it, carId) }
            .launchIn(scope)

        highTemp
            .debounce(100.milliseconds)
            .onEach { database.updateHighTemp(it, carId) }
            .launchIn(scope)
    }
}

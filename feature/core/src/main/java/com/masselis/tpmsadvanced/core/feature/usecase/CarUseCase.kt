package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.car.interfaces.CarDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

internal class CarUseCase @Inject constructor(
    private val carId: UUID,
    private val database: CarDatabase
) {
    fun carFlow() = database.selectByUuid(carId).distinctUntilChanged()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun delete() {
        database.setIsCurrent(database.selectAll().first { it.uuid != carId }.uuid, true)
        GlobalScope.launch {
            delay(1.seconds)
            database.delete(carId)
        }
    }
}

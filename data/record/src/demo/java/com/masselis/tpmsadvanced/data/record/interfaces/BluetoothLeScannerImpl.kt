package com.masselis.tpmsadvanced.data.record.interfaces

import com.masselis.tpmsadvanced.core.common.now
import com.masselis.tpmsadvanced.data.record.ioc.SingleInstance
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.record.model.Tyre
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@SingleInstance
internal class BluetoothLeScannerImpl @Inject constructor() : BluetoothLeScanner {

    private val pressures = listOf(
        0.4f.bar,
        1.6f.bar,
        2.0f.bar,
        2.8f.bar,
        4.0f.bar
    )

    private val temperatures = listOf(
        15f.celsius,
        20f.celsius,
        35f.celsius,
        70f.celsius,
        95f.celsius
    )

    private val random = Random(9847946)

    private fun createTyre() = Tyre(
        now(),
        TyreLocation.values().random(random),
        (1..4).random(random),
        pressures.random(random),
        temperatures.random(random),
        100u,
        false
    )

    private val startTyres = mutableMapOf<TyreLocation, Tyre>().apply {
        while (TyreLocation.values().size != count()) {
            val tyre = createTyre()
            put(tyre.location, tyre)
        }
    }

    private val source = (0..Int.MAX_VALUE)
        .asFlow()
        .transform {
            emit(createTyre())
            delay(5.seconds)
        }
        .onStart { emitAll(startTyres.values.asFlow()) }
        .shareIn(
            CoroutineScope(EmptyCoroutineContext),
            SharingStarted.Lazily,
            startTyres.size + 1
        )

    override fun highDutyScan(): Flow<Tyre> = source

    override fun normalScan(): Flow<Tyre> = source
}
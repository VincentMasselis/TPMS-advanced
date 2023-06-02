package com.masselis.tpmsadvanced.feature.background.usecase

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureBackgroundComponent.Scope
internal class ForegroundServiceUseCase @Inject constructor(
    private val vehicleDatabase: VehicleDatabase
) {

    init {
        combine(
            callbackFlow<Boolean> {
                val lifecycle = ProcessLifecycleOwner.get().lifecycle
                send(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                val observer = object : DefaultLifecycleObserver {
                    override fun onStart(owner: LifecycleOwner) {
                        launch { send(true) }
                    }

                    override fun onStop(owner: LifecycleOwner) {
                        launch { send(false) }
                    }
                }
                lifecycle.addObserver(observer)
                awaitClose { lifecycle.removeObserver(observer) }
            }.flowOn(Main),
            vehicleDatabase.selectAllFlow().map { vehicles ->
                vehicles
                    .map { it.uuid to it.isBackgroundMonitor }
                    .sortedBy { (uuid) -> uuid }
            }
        ) { isAppVisible, vehicles ->
            if (isAppVisible) vehicles.map { (uuid) -> uuid } to emptyList()
            else vehicles
                .groupBy { (_, isBackgroundMonitor) -> isBackgroundMonitor }
                .let {
                    Pair(
                        it.getValue(false).map { (uuid) -> uuid },
                        it.getValue(true).map { (uuid) -> uuid }
                    )
                }
        }.distinctUntilChanged()
            .onEach { (stop, start) ->
                stop.forEach { uuid ->
                    // TODO appContext.stopService()
                }
                start.forEach { uuid ->
                    // TODO appContext.startForegroundService()
                }
            }
            .launchIn(GlobalScope + IO)
    }
}

package com.masselis.tpmsadvanced.feature.background.interfaces

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundVehicleComponent
import com.masselis.tpmsadvanced.feature.background.ioc.InternalComponent
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

internal class MonitorService : Service() {

    @Inject
    internal lateinit var vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    private val monitoring = mutableListOf<BackgroundVehicleComponent>()
    private val mutex = Mutex()
    private lateinit var scope: CoroutineScope

    init {
        InternalComponent.inject(this)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(Dispatchers.Default)
        vehiclesToMonitorUseCase
            .appVisibilityIgnoredAndMonitored()
            .onEach { (ignored, monitored) ->
                mutex.withLock {
                    // Removes entries from `monitoring`
                    monitoring.removeIf { monitoring ->
                        ignored.any { it.uuid == monitoring.vehicle.uuid }
                            .also { toRemove -> if (toRemove) monitoring.scope.cancel() }
                    }

                    // Check if any `ServiceNotifier` is working has foreground service
                    val hasForegroundService = monitoring.any { it.foregroundService != null }

                    // Add entries into `monitoring`
                    monitoring.map { comp -> comp.vehicle.uuid }
                        .let { monitoringUuids ->
                            monitored.filter { monitoringUuids.contains(it.uuid).not() }
                        }
                        .mapIndexed { index, vehicle ->
                            if (index == 0 && hasForegroundService.not())
                                BackgroundVehicleComponent(this, vehicle)
                            else
                                BackgroundVehicleComponent(null, vehicle)
                        }
                        .forEach { monitoring.add(it) }
                }
            }
            .onCompletion { mutex.withLock { monitoring.clear() } }
            .launchIn(scope)
    }

    override fun onDestroy() {
        scope.cancel(null)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

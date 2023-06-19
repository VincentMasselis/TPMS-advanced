package com.masselis.tpmsadvanced.feature.background.interfaces

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundVehicleComponent
import com.masselis.tpmsadvanced.feature.background.ioc.DaggerBackgroundVehicleComponent
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import okio.withLock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

public class MonitorService : Service() {

    @Inject
    internal lateinit var vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    private val monitoring = mutableListOf<BackgroundVehicleComponent>()
    private val lock = ReentrantLock()
    private lateinit var scope: CoroutineScope

    init {
        FeatureBackgroundComponent.inject(this)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(Dispatchers.Default)
        vehiclesToMonitorUseCase
            .realtimeIgnoredAndMonitored()
            .onEach { (ignored, monitored) ->
                lock.withLock {
                    // Removes entries from `monitoring`
                    monitoring.removeIf { monitoring ->
                        ignored.any { it.uuid == monitoring.vehicle.uuid }
                            .also { toRemove -> if (toRemove) monitoring.release() }
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
                                DaggerBackgroundVehicleComponent.factory()
                                    .build(this, vehicle)
                            else
                                DaggerBackgroundVehicleComponent.factory()
                                    .build(null, vehicle)
                        }
                        .forEach { monitoring.add(it) }
                }
            }
            .onCompletion { lock.withLock { monitoring.clear() } }
            .launchIn(scope)
    }

    override fun onDestroy() {
        scope.cancel(null)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

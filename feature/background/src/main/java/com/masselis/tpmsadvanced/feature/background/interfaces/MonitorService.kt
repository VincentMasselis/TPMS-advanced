package com.masselis.tpmsadvanced.feature.background.interfaces

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundVehicleComponent
import com.masselis.tpmsadvanced.feature.background.ioc.DaggerBackgroundVehicleComponent
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject

public class MonitorService : Service() {

    @Inject
    internal lateinit var vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    @Inject
    internal lateinit var vehicleComponentFactory: VehicleComponent.Factory

    private val monitoring = mutableListOf<BackgroundVehicleComponent>()
    private lateinit var scope: CoroutineScope

    init {
        FeatureBackgroundComponent.inject(this)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(Dispatchers.Default)
        vehiclesToMonitorUseCase
            .ignoredAndMonitored()
            .onEach { (ignored, monitored) ->
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
                    .map {
                        vehicleComponentFactory.build(
                            it,
                            scope + Job(scope.coroutineContext[Job]) // Creates a child scope
                        )
                    }
                    .mapIndexed { index, it ->
                        if (index == 0 && hasForegroundService.not())
                            DaggerBackgroundVehicleComponent.factory().build(this, it)
                        else
                            DaggerBackgroundVehicleComponent.factory().build(null, it)
                    }
                    .forEach { monitoring.add(it) }
            }
            .launchIn(scope)
    }

    override fun onDestroy() {
        scope.cancel(null)
        monitoring.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
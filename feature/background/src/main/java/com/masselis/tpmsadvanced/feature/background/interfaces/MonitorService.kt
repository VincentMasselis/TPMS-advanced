package com.masselis.tpmsadvanced.feature.background.interfaces

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MIN
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.feature.background.R
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

public class MonitorService : Service() {

    @Inject
    internal lateinit var vehiclesToMonitorUseCase: VehiclesToMonitorUseCase
    private val notificationManager = NotificationManagerCompat.from(appContext)
    private lateinit var scope: CoroutineScope

    init {
        FeatureBackgroundComponent.inject(this)
        NotificationManagerCompat.from(appContext).createNotificationChannel(
            NotificationChannelCompat
                .Builder(channelName, IMPORTANCE_MIN)
                .setName("Monitor service")
                .build()
        )
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(Dispatchers.Main)
        vehiclesToMonitorUseCase
            .ignoredAndMonitored()
            .onEach { (ignored, monitored) ->
                monitored.forEachIndexed { index, vehicle ->
                    if (index == 0)
                        startForeground(vehicle.uuid.hashCode(), notification(vehicle))
                    else
                        notificationManager.notify(vehicle.uuid.hashCode(), notification(vehicle))
                }
                ignored.forEach { notificationManager.cancel(it.uuid.hashCode()) }
            }
            .launchIn(scope)
    }

    override fun onDestroy() {
        scope.cancel(null)
        super.onDestroy()
    }

    private fun notification(vehicle: Vehicle) = NotificationCompat
        .Builder(appContext, channelName)
        .setSmallIcon(R.drawable.car_tire_alert)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setContentText("Monitoring ${vehicle.name}")
        .build()

    override fun onBind(intent: Intent?): IBinder? = null

    internal companion object {
        private const val channelName = "MONITOR_SERVICE"
    }
}

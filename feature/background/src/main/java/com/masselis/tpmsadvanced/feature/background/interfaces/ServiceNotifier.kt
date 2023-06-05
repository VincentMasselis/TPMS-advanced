package com.masselis.tpmsadvanced.feature.background.interfaces

import android.annotation.SuppressLint
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MIN
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.feature.background.R
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.TyreAtmosphere
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundVehicleComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Named

@SuppressLint("MissingPermission")
@BackgroundVehicleComponent.Scope
internal class ServiceNotifier @Inject constructor(
    @Named("base") vehicle: Vehicle,
    scope: CoroutineScope,
    findTyreComponentUseCase: FindTyreComponentUseCase,
    vehicleRangesUseCase: VehicleRangesUseCase,
) {
    private val notificationManager = NotificationManagerCompat.from(appContext)

    init {
        notificationManager.createNotificationChannel(
            NotificationChannelCompat
                .Builder(channelName, IMPORTANCE_MIN)
                .setName("Monitor service")
                .build()
        )

        vehicle
            .kind
            .locations
            .map { it to findTyreComponentUseCase.find(it) }
            .let { comps ->
                combine(
                    combine(comps.map { it.second.tyreAtmosphereUseCase.listen() }) { it },
                    vehicleRangesUseCase.lowTemp,
                    vehicleRangesUseCase.highTemp,
                    vehicleRangesUseCase.lowPressure,
                    vehicleRangesUseCase.highPressure,
                ) { atmospheres, lowTemp, highTemp, lowPressure, highPressure ->
                    atmospheres.filterNot {
                        it.temperature in lowTemp..highTemp ||
                                it.pressure in lowPressure..highPressure
                    }
                }
            }
            .onEach { badAtmospheres ->
                notificationManager.notify(
                    vehicle.uuid.hashCode(),
                    notification(vehicle, badAtmospheres)
                )
            }
            .onCompletion { notificationManager.cancel(vehicle.uuid.hashCode()) }
            .launchIn(scope)
    }

    private fun notification(vehicle: Vehicle, badAtmospheres: List<TyreAtmosphere>) =
        NotificationCompat
            .Builder(appContext, channelName)
            .setSmallIcon(R.drawable.car_tire_alert)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentText("Monitoring ${vehicle.name}")
            .build()

    internal companion object {
        private const val channelName = "MONITOR_SERVICE"
    }
}
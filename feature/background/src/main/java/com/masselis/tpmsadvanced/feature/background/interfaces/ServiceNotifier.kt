package com.masselis.tpmsadvanced.feature.background.interfaces

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MAX
import androidx.core.app.ServiceCompat.STOP_FOREGROUND_REMOVE
import androidx.core.app.ServiceCompat.stopForeground
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.feature.background.R
import com.masselis.tpmsadvanced.core.feature.usecase.FindTyreComponentUseCase
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleRangesUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.TyreAtmosphere
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier.State.*
import com.masselis.tpmsadvanced.feature.background.ioc.BackgroundVehicleComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@SuppressLint("MissingPermission")
@BackgroundVehicleComponent.Scope
internal class ServiceNotifier @Inject constructor(
    @Named("base") vehicle: Vehicle,
    scope: CoroutineScope,
    findTyreComponentUseCase: FindTyreComponentUseCase,
    vehicleRangesUseCase: VehicleRangesUseCase,
    unitPreferences: UnitPreferences,
    foregroundService: Service?,
) {
    private val notificationManager = NotificationManagerCompat.from(appContext)

    init {
        notificationManager.createNotificationChannel(
            NotificationChannelCompat
                .Builder(channelNameWhenOk, IMPORTANCE_LOW)
                .setName("Monitor service when tyres are OK")
                .build()
        )
        notificationManager.createNotificationChannel(
            NotificationChannelCompat
                .Builder(channelNameForAlerts, IMPORTANCE_MAX)
                .setName("Monitor service when alerting")
                .build()
        )

        vehicle
            .kind
            .locations
            .map { it to findTyreComponentUseCase.find(it) }
            .let { comps ->
                combine(
                    combine(comps.map { it.second.tyreAtmosphereUseCase.listen() }) { it }
                        .onStart { emit(emptyArray()) }
                        .debounce(300.milliseconds),
                    vehicleRangesUseCase.highTemp,
                    vehicleRangesUseCase.lowPressure,
                    vehicleRangesUseCase.highPressure,
                ) { atmospheres, highTemp, lowPressure, highPressure ->
                    atmospheres
                        .firstOrNull { it.pressure !in lowPressure..highPressure }
                        ?.let(::PressureAlert)
                        ?: atmospheres
                            .firstOrNull { it.temperature > highTemp }
                            ?.let(::TemperatureAlert)
                        ?: NoAlert
                }
            }
            .distinctUntilChanged()
            .map { state ->
                NotificationCompat
                    .Builder(
                        appContext,
                        when (state) {
                            NoAlert -> channelNameWhenOk
                            is PressureAlert, is TemperatureAlert -> channelNameForAlerts
                        }
                    )
                    .setSmallIcon(R.drawable.car_tire_alert)
                    .setPriority(
                        when (state) {
                            NoAlert -> PRIORITY_LOW
                            is PressureAlert, is TemperatureAlert -> PRIORITY_MAX
                        }
                    )
                    .setContentText(
                        when (state) {
                            NoAlert -> "Your tyres are OK"
                            is PressureAlert -> "⚠️ A tyre reached the pressure of ${
                                state.atmosphere.pressure.string(unitPreferences.pressure.value)
                            } !!!"

                            is TemperatureAlert -> "⚠️ A tyre reached the temperature of ${
                                state.atmosphere.temperature.string(unitPreferences.temperature.value)
                            } !!!"
                        }
                    )
                    .setContentIntent(
                        PendingIntent.getActivity(
                            appContext,
                            0,
                            Intent().apply {
                                component = ComponentName(
                                    appContext,
                                    "com.masselis.tpmsadvanced.interfaces.RootActivity"
                                )
                            },
                            FLAG_IMMUTABLE,
                        )
                    )
                    .setSubText(vehicle.name)
                    .build()
            }
            .onEach {
                foregroundService?.startForeground(vehicle.uuid.hashCode(), it)
                    ?: notificationManager.notify(vehicle.uuid.hashCode(), it)
            }
            .onCompletion {
                foregroundService?.let { stopForeground(it, STOP_FOREGROUND_REMOVE) }
                    ?: notificationManager.cancel(vehicle.uuid.hashCode())
            }
            .launchIn(scope)
    }

    sealed interface State {
        object NoAlert : State

        @JvmInline
        value class PressureAlert(val atmosphere: TyreAtmosphere) : State

        @JvmInline
        value class TemperatureAlert(val atmosphere: TyreAtmosphere) : State
    }

    internal companion object {
        private const val channelNameWhenOk = "MONITOR_SERVICE_WHEN_OK"
        private const val channelNameForAlerts = "MONITOR_SERVICE_FOR_ALERT"
    }
}
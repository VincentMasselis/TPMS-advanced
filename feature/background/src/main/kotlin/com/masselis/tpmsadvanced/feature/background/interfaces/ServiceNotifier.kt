package com.masselis.tpmsadvanced.feature.background.interfaces

import android.annotation.SuppressLint
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getBroadcast
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MAX
import androidx.core.app.ServiceCompat
import androidx.core.app.ServiceCompat.STOP_FOREGROUND_REMOVE
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.model.TyreAtmosphere
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.R
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier.State.NoAlert
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier.State.PressureAlert
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier.State.ScanFailure
import com.masselis.tpmsadvanced.feature.background.interfaces.ServiceNotifier.State.TemperatureAlert
import com.masselis.tpmsadvanced.feature.main.ioc.tyre.TyreComponent
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@SuppressLint("MissingPermission")
internal class ServiceNotifier(
    vehicle: Vehicle,
    scope: CoroutineScope,
    tyreComponent: (Vehicle.Kind.Location) -> TyreComponent,
    vehicleRangesUseCase: VehicleRangesUseCase,
    unitPreferences: UnitPreferences,
    service: Service,
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
            .map { tyreComponent(it) }
            .let { comps ->
                combine(
                    combine(comps.map { it.tyreAtmosphereUseCase.listen() }) { it }
                        .onStart { emit(emptyArray()) }
                        .debounce(100.milliseconds),
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
            .catch { if (it is BluetoothLeScanner.ScanFailed) emit(ScanFailure) else throw it }
            .distinctUntilChanged()
            .map { state ->
                NotificationCompat
                    .Builder(
                        appContext,
                        when (state) {
                            NoAlert -> channelNameWhenOk
                            is PressureAlert, is TemperatureAlert, ScanFailure -> channelNameForAlerts
                        }
                    )
                    .setSmallIcon(
                        when (state) {
                            NoAlert -> R.drawable.car_tire
                            is PressureAlert, is TemperatureAlert, ScanFailure -> R.drawable.car_tire_alert
                        }
                    )
                    .setPriority(
                        when (state) {
                            NoAlert -> PRIORITY_LOW
                            is PressureAlert, is TemperatureAlert, ScanFailure -> PRIORITY_MAX
                        }
                    )
                    .setSubText(vehicle.name)
                    .setContentText(
                        when (state) {
                            NoAlert -> "Your tyres are OK"
                            is PressureAlert -> "⚠️ A tyre reached the pressure of ${
                                state.atmosphere.pressure.string(unitPreferences.pressure.value)
                            } !!!"

                            is TemperatureAlert -> "⚠️ A tyre reached the temperature of ${
                                state.atmosphere.temperature.string(unitPreferences.temperature.value)
                            } !!!"

                            ScanFailure -> "The Android system reported an issue during the" +
                                    " bluetooth scan, TPMS Advanced must be restarted"
                        }
                    )
                    .apply {
                        when (state) {
                            NoAlert, is PressureAlert, is TemperatureAlert ->
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "tpmsadvanced://vehicle/${vehicle.uuid}".toUri(),
                                ).let {
                                    TaskStackBuilder
                                        .create(appContext)
                                        .addNextIntentWithParentStack(it)
                                        .getPendingIntent(
                                            vehicle.uuid.hashCode(),
                                            FLAG_IMMUTABLE
                                        )
                                }.also(::setContentIntent)

                            ScanFailure -> {
                                // Nothing to do, the intent does nothing when clicked
                            }
                        }

                    }
                    .addAction(
                        when (state) {
                            NoAlert, is PressureAlert, is TemperatureAlert ->
                                NotificationCompat.Action.Builder(
                                    null,
                                    "Stop",
                                    getBroadcast(
                                        appContext,
                                        vehicle.uuid.hashCode(),
                                        DisableMonitorBroadcastReceiver.intent(),
                                        FLAG_IMMUTABLE
                                    )
                                ).build()

                            ScanFailure ->
                                NotificationCompat.Action.Builder(
                                    null,
                                    "Restart app",
                                    getBroadcast(
                                        appContext,
                                        vehicle.uuid.hashCode(),
                                        RestartAppBroadcastReceiver.intent(),
                                        FLAG_IMMUTABLE
                                    )
                                ).build()
                        }

                    )
                    .setAutoCancel(false)
                    .build()
            }
            .onEach {
                ServiceCompat.startForeground(
                    service,
                    vehicle.uuid.hashCode(),
                    it,
                    // https://developer.android.com/about/versions/14/changes/fgs-types-required#connected-device
                    if (SDK_INT >= Q) FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE else 0
                )
            }
            .launchIn(scope)

        callbackFlow<Nothing> {
            awaitClose {
                service.also { stopForeground(it, STOP_FOREGROUND_REMOVE) }
            }
        }.launchIn(scope)
    }

    sealed interface State {
        data object NoAlert : State

        @JvmInline
        value class PressureAlert(val atmosphere: TyreAtmosphere) : State

        @JvmInline
        value class TemperatureAlert(val atmosphere: TyreAtmosphere) : State

        data object ScanFailure : State
    }

    @Suppress("ConstPropertyName")
    internal companion object {
        private const val channelNameWhenOk = "MONITOR_SERVICE_WHEN_OK"
        private const val channelNameForAlerts = "MONITOR_SERVICE_FOR_ALERT"
    }
}

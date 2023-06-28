package com.masselis.tpmsadvanced.feature.shortcut.ioc

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.content.pm.ShortcutManagerCompat.FLAG_MATCH_PINNED
import androidx.core.net.toUri
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.feature.shortcut.usecase.FeatureShortcutComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureShortcutComponent.Scope
internal class ShortcutUseCase @Inject constructor(
    vehicleListUseCase: VehicleListUseCase,
) {

    init {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(appContext))
            vehicleListUseCase
                .vehicleListFlow
                .onEach { vehicles ->
                    val uuids = vehicles.map { it.uuid.toString() }
                    ShortcutManagerCompat.getShortcuts(appContext, FLAG_MATCH_PINNED)
                        .filter { uuids.contains(it.id).not() }
                        .map { it.id }
                        .also {
                            ShortcutManagerCompat.disableShortcuts(
                                appContext,
                                it,
                                "This vehicle is not available anymore"
                            )
                        }
                    vehicles
                        .map { vehicle ->
                            ShortcutInfoCompat.Builder(appContext, vehicle.uuid.toString())
                                .setShortLabel(vehicle.name)
                                .setIntent(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        "tpmsadvanced://vehicle/${vehicle.uuid}".toUri(),
                                    )
                                )
                                .build()
                        }
                        .forEach { shortcut ->
                            ShortcutManagerCompat.requestPinShortcut(
                                appContext,
                                shortcut,
                                ShortcutManagerCompat
                                    .createShortcutResultIntent(appContext, shortcut)
                                    .let {
                                        PendingIntent.getBroadcast(
                                            appContext,
                                            shortcut.id.hashCode(),
                                            it,
                                            FLAG_IMMUTABLE
                                        )
                                    }
                                    .intentSender
                            )
                        }
                }
                .launchIn(GlobalScope)
    }
}

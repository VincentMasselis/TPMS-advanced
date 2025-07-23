package com.masselis.tpmsadvanced.feature.shortcut.usecase

import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.feature.shortcut.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(DelicateCoroutinesApi::class)
internal class ShortcutUseCase(
    vehicleListUseCase: VehicleListUseCase,
) {

    init {
        vehicleListUseCase
            .vehicleListFlow
            .onEach { vehicles ->
                ShortcutManagerCompat.setDynamicShortcuts(
                    appContext,
                    vehicles.map { vehicle ->
                        ShortcutInfoCompat.Builder(appContext, vehicle.uuid.toString())
                            .setIcon(
                                IconCompat.createWithResource(
                                    appContext, when (vehicle.kind) {
                                        Vehicle.Kind.CAR -> R.drawable.car_convertible
                                        Vehicle.Kind.SINGLE_AXLE_TRAILER -> R.drawable.truck_trailer
                                        Vehicle.Kind.MOTORCYCLE -> R.drawable.motorbike
                                        Vehicle.Kind.TADPOLE_THREE_WHEELER -> R.drawable.atv
                                        Vehicle.Kind.DELTA_THREE_WHEELER -> R.drawable.atv
                                    }
                                )
                            )
                            .setShortLabel(vehicle.name)
                            .setIntent(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "tpmsadvanced://vehicle/${vehicle.uuid}".toUri(),
                                )
                            )
                            .build()
                    }
                )
            }
            .launchIn(GlobalScope)
    }
}

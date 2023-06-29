package com.masselis.tpmsadvanced.feature.shortcut.ioc

import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.feature.usecase.VehicleListUseCase
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.*
import com.masselis.tpmsadvanced.feature.shortcut.R
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
                                        CAR -> R.drawable.car_convertible
                                        SINGLE_AXLE_TRAILER -> R.drawable.truck_trailer
                                        MOTORCYCLE -> R.drawable.motorbike
                                        TADPOLE_THREE_WHEELER -> R.drawable.atv
                                        DELTA_THREE_WHEELER -> R.drawable.atv
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

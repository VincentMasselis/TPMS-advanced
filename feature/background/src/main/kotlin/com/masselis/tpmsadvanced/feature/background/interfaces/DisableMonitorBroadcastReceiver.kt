package com.masselis.tpmsadvanced.feature.background.interfaces

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.feature.background.ioc.InternalComponent
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import java.util.UUID
import javax.inject.Inject

internal class DisableMonitorBroadcastReceiver internal constructor() : BroadcastReceiver() {

    @Inject
    internal lateinit var vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    init {
        InternalComponent.inject(this)
    }

    override fun onReceive(context: Context, intent: Intent): Unit = intent
        .getStringExtra(EXTRA_VEHICLE_UUID)!!
        .let { UUID.fromString(it) }
        .let(vehiclesToMonitorUseCase::disableManual)

    internal companion object {
        fun intent(vehicleUUID: UUID) =
            Intent(appContext, DisableMonitorBroadcastReceiver::class.java)
                .apply { putExtra(EXTRA_VEHICLE_UUID, vehicleUUID.toString()) }

        private const val EXTRA_VEHICLE_UUID = "VEHICLE_UUID"
    }
}

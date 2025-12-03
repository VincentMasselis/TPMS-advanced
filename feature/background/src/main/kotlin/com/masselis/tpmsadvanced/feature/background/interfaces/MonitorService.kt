package com.masselis.tpmsadvanced.feature.background.interfaces

import android.content.Intent
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.background.ioc.vehicle.ServiceComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class MonitorService : LifecycleService() {

    private lateinit var component: ServiceComponent

    override fun onCreate() {
        isRunningMutableStateFlow.value = true
        super.onCreate()
    }

    override fun onStartCommand(
        intent: Intent?, flags: Int, startId: Int
    ): Int {
        requireNotNull(intent)
        super.onStartCommand(intent, flags, startId)
        component = ServiceComponent.Factory(
            getParcelableExtra(intent, paramVehicle, Vehicle::class.java)!!,
            this,
            lifecycleScope,
        )
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningMutableStateFlow.value = false
    }

    @Suppress("ConstPropertyName")
    companion object {
        private val isRunningMutableStateFlow = MutableStateFlow(false)
        val isRunning = isRunningMutableStateFlow.asStateFlow()

        private const val paramVehicle = "VEHICLE_PARAM"
        fun intent(vehicle: Vehicle) = Intent(appContext, MonitorService::class.java)
            .apply { putExtra(paramVehicle, vehicle) }

    }
}

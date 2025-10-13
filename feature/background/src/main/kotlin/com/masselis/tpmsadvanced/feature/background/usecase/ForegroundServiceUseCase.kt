package com.masselis.tpmsadvanced.feature.background.usecase

import android.content.Intent
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

@OptIn(DelicateCoroutinesApi::class)
internal class ForegroundServiceUseCase(
    vehiclesToMonitorUseCase: VehiclesToMonitorUseCase,
    checkForPermissionUseCase: CheckForPermissionUseCase,
) {

    init {
        vehiclesToMonitorUseCase.appVisibilityIgnoredAndMonitored()
            .map { (_, monitored) -> monitored.isNotEmpty() }
            .flowOn(IO)
            .distinctUntilChanged()
            .onEach { isMonitorRequired ->
                if (checkForPermissionUseCase.isGrant() && isMonitorRequired)
                    appContext.startService(serviceIntent)
                else
                    appContext.stopService(serviceIntent)
            }
            .launchIn(GlobalScope + Main.immediate)
    }

    companion object {
        private val serviceIntent by lazy { Intent(appContext, MonitorService::class.java) }
    }
}

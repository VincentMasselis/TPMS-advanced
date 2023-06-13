package com.masselis.tpmsadvanced.feature.background.usecase

import android.content.Intent
import androidx.core.content.ContextCompat.startForegroundService
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class, FlowPreview::class)
@FeatureBackgroundComponent.Scope
internal class ForegroundServiceUseCase @Inject constructor(
    vehiclesToMonitorUseCase: VehiclesToMonitorUseCase,
    checkForPermissionUseCase: CheckForPermissionUseCase,
) {

    init {
        vehiclesToMonitorUseCase.realtimeIgnoredAndMonitored()
            .map { (_, monitored) -> monitored.isNotEmpty() }
            .distinctUntilChanged()
            .flowOn(IO)
            .onEach { isMonitorRequired ->
                if (checkForPermissionUseCase.isPermissionGrant() && isMonitorRequired)
                    startForegroundService(appContext, serviceIntent)
                else
                    appContext.stopService(serviceIntent)
            }
            .launchIn(GlobalScope + Main.immediate)
    }

    companion object {
        private val serviceIntent by lazy { Intent(appContext, MonitorService::class.java) }
    }
}

package com.masselis.tpmsadvanced.feature.background.usecase

import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureBackgroundComponent.Scope
internal class ForegroundServiceUseCase @Inject constructor(
    vehiclesToMonitorUseCase: VehiclesToMonitorUseCase,
    checkForPermissionUseCase: CheckForPermissionUseCase,
) {

    init {
        combine(
            callbackFlow<Boolean> {
                val lifecycle = ProcessLifecycleOwner.get().lifecycle
                send(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                val observer = object : DefaultLifecycleObserver {
                    override fun onStart(owner: LifecycleOwner) {
                        launch { send(true) }
                    }

                    override fun onStop(owner: LifecycleOwner) {
                        launch { send(false) }
                    }
                }
                lifecycle.addObserver(observer)
                awaitClose { lifecycle.removeObserver(observer) }
            }.flowOn(Main),
            vehiclesToMonitorUseCase.ignoredAndMonitored()
                .map { (_, monitored) -> monitored.isNotEmpty() }
                .distinctUntilChanged()
        ) { isAppVisible, isMonitorRequired ->
            if (checkForPermissionUseCase.isPermissionGrant()
                && isAppVisible.not()
                && isMonitorRequired
            ) appContext.startForegroundService(serviceIntent)
            else
                appContext.stopService(serviceIntent)
        }.launchIn(GlobalScope + IO)
    }

    companion object {
        private val serviceIntent by lazy { Intent(appContext, MonitorService::class.java) }
    }
}

package com.masselis.tpmsadvanced.feature.background.usecase

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import dagger.Lazy
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureBackgroundComponent.Scope
internal class CheckForPermissionUseCase @Inject constructor(
    database: VehicleDatabase,
    foregroundServiceUseCase: Lazy<ForegroundServiceUseCase>
) {

    val requiredPermission =
        if (SDK_INT >= TIRAMISU) POST_NOTIFICATIONS
        else null

    init {
        // The rules are simple:
        // * If the permission is revoked every `isBackgroundMonitor` must be set to `false` and
        //   the apps always restarts
        // * If the permission is grant, `isBackgroundMonitor` could be `true` or `false`
        // * A permission could be grant at runtime
        GlobalScope.launch(IO) {
            if (isPermissionGrant().not()) {
                database.updateEveryIsBackgroundMonitorToFalse()
                // If any `isBackgroundMonitor` is set to true, that's mean the permission was grant
                database.selectAllIsBackgroundMonitor()
                    .first { it.any { isMonitoring -> isMonitoring } }
            }
            // ForegroundServiceUseCase can only run if the permission is granted
            foregroundServiceUseCase.get()
        }
    }

    fun isPermissionGrant() = requiredPermission
        ?.let { checkSelfPermission(appContext, it) == PERMISSION_GRANTED }
        ?: true
}

package com.masselis.tpmsadvanced.feature.background.usecase

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.car.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.feature.background.ioc.FeatureBackgroundComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@FeatureBackgroundComponent.Scope
internal class CheckForPermissionUseCase @Inject constructor(
    database: VehicleDatabase,
) {

    val requiredPermission =
        if (SDK_INT >= TIRAMISU) POST_NOTIFICATIONS
        else null

    init {
        GlobalScope.launch(IO) {
            if (isPermissionGrant().not())
                database.updateEveryIsBackgroundMonitorToFalse()
        }
    }

    fun isPermissionGrant() = requiredPermission
        ?.let { checkSelfPermission(appContext, it) == PERMISSION_GRANTED }
        ?: true
}

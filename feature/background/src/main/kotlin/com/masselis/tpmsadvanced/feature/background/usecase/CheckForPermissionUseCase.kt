package com.masselis.tpmsadvanced.feature.background.usecase

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
internal class CheckForPermissionUseCase(
    database: VehicleDatabase,
) {

    init {
        GlobalScope.launch(IO) {
            if (isGrant().not())
                database.updateEveryIsBackgroundMonitorToFalse()
        }
    }

    @SuppressLint("NewApi")
    fun missingPermission() =
        if (isGrant().not()) POST_NOTIFICATIONS
        else null

    fun isGrant(): Boolean {
        if (SDK_INT < TIRAMISU) return true

        return checkSelfPermission(appContext, POST_NOTIFICATIONS) == PERMISSION_GRANTED
    }
}

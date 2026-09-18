package com.masselis.tpmsadvanced.data.app.interfaces

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.edit
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.common.observableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow

public class AppPreferences internal constructor(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        "APP",
        Context.MODE_PRIVATE
    )

    public val showTimestamp: MutableStateFlow<Boolean> = observableStateFlow(
        sharedPreferences.getBoolean("SHOW_TIMESTAMP", false)
    ) { _, newValue ->
        sharedPreferences.edit { putBoolean("SHOW_TIMESTAMP", newValue) }
    }

    public val showSensorId: MutableStateFlow<Boolean> = observableStateFlow(
        sharedPreferences.getBoolean("SHOW_SENSOR_ID", false)
    ) { _, newValue ->
        sharedPreferences.edit { putBoolean("SHOW_SENSOR_ID", newValue) }
    }

    public val showTimeSinceUpdate: MutableStateFlow<Boolean> = observableStateFlow(
        sharedPreferences.getBoolean("SHOW_TIME_SINCE_UPDATE", true)
    ) { _, newValue ->
        sharedPreferences.edit { putBoolean("SHOW_TIME_SINCE_UPDATE", newValue) }
    }

    private val packageInfo
        get() = appContext
            .packageManager
            .run {
                if (SDK_INT >= TIRAMISU) {
                    getPackageInfo(appContext.packageName, PackageManager.PackageInfoFlags.of(0))
                } else {
                    getPackageInfo(appContext.packageName, 0)
                }
            }!!

    public val previousVersionCode: Long? = sharedPreferences
        .getLong("VC", Long.MIN_VALUE)
        .takeIf { it != Long.MIN_VALUE }

    public val currentVersionCode: Long = packageInfo.longVersionCode

    public val isFreshInstallation: Boolean =
        packageInfo.let { it.firstInstallTime == it.lastUpdateTime }

    init {
        if (currentVersionCode != previousVersionCode)
            sharedPreferences.edit { putLong("VC", currentVersionCode) }
    }
}

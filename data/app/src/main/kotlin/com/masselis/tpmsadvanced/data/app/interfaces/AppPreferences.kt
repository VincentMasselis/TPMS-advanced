package com.masselis.tpmsadvanced.data.app.interfaces

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.edit
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.app.ioc.DataAppComponent
import javax.inject.Inject

@DataAppComponent.Scope
public class AppPreferences @Inject internal constructor(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        "APP",
        Context.MODE_PRIVATE
    )

    public val previousVersionCode: Long? = sharedPreferences
        .getLong("VC", Long.MIN_VALUE)
        .takeIf { it != Long.MIN_VALUE }

    @Suppress("DEPRECATION")
    public val runningVersionCode: Long = appContext
        .packageManager
        .run {
            if (SDK_INT >= TIRAMISU) {
                getPackageInfo(appContext.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                getPackageInfo(appContext.packageName, 0)
            }
        }
        .let { packageInfo ->
            if (SDK_INT >= P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        }

    init {
        runningVersionCode.also {
            if (it != previousVersionCode)
                sharedPreferences.edit { putLong("VC", it) }
        }
    }

    @Suppress("DEPRECATION")
    public val isFreshInstallation: Boolean = appContext
        .packageManager
        .run {
            if (SDK_INT >= TIRAMISU) {
                getPackageInfo(appContext.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                getPackageInfo(appContext.packageName, 0)
            }
        }
        .let { it.firstInstallTime == it.lastUpdateTime }
}

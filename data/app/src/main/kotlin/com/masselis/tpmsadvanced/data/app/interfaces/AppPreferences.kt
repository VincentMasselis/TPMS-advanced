package com.masselis.tpmsadvanced.data.app.interfaces

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.edit
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.app.BuildConfig.VERSION_CODE

public class AppPreferences internal constructor(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        "APP",
        Context.MODE_PRIVATE
    )

    public val previousVersionCode: Long? = sharedPreferences
        .getLong("VC", Long.MIN_VALUE)
        .takeIf { it != Long.MIN_VALUE }

    init {
        if (VERSION_CODE.toLong() != previousVersionCode)
            sharedPreferences.edit { putLong("VC", VERSION_CODE.toLong()) }
    }

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

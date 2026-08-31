package com.masselis.tpmsadvanced.core.ui

import android.content.Context
import android.content.Intent

public fun Context.restartApp() {
    packageManager.getLaunchIntentForPackage(packageName)
        ?.component
        ?.let(Intent::makeRestartActivityTask)
        ?.also(::startActivity)
    Runtime.getRuntime().exit(0)
}

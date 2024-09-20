package com.masselis.tpmsadvanced.feature.background.interfaces

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.ui.restartApp

internal class RestartAppBroadcastReceiver internal constructor() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) = context.restartApp()

    internal companion object {
        fun intent() = Intent(appContext, RestartAppBroadcastReceiver::class.java)
    }
}

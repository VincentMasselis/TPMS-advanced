package com.masselis.tpmsadvanced.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.masselis.tpmsadvanced.core.BuildConfig


class FirebaseInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Firebase.initialize(context)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not())
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
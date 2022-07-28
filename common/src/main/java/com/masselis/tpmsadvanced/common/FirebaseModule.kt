package com.masselis.tpmsadvanced.common

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.Module
import dagger.Provides

@Module
object FirebaseModule {

    private val firebaseApp = Firebase.initialize(appContext)
    private val crashlytics = firebaseApp?.let { Firebase.crashlytics }

    init {
        crashlytics?.setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not())
    }

    @Provides
    fun firebaseApp() = firebaseApp

    @Provides
    fun crashlytics() = crashlytics
}
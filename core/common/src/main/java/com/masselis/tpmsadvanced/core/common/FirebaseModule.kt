package com.masselis.tpmsadvanced.core.common

import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.Module
import dagger.Provides

@Module
public object FirebaseModule {

    private val firebaseApp = Firebase.initialize(appContext)
    private val crashlytics = firebaseApp?.let { Firebase.crashlytics }

    init {
        crashlytics?.setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not())
    }

    @Provides
    public fun firebaseApp(): FirebaseApp? = firebaseApp

    @Provides
    public fun crashlytics(): FirebaseCrashlytics? = crashlytics
}

package com.masselis.tpmsadvanced.core.common

import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import dagger.Module
import dagger.Provides

@Module
internal object FirebaseModule {

    private val firebaseApp = Firebase.initialize(appContext)
    private val crashlytics = firebaseApp?.let { Firebase.crashlytics }

    init {
        firebaseApp?.setDataCollectionDefaultEnabled(BuildConfig.DEBUG.not() as Boolean?)
        crashlytics?.setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not())
    }

    @Provides
    fun firebaseApp(): FirebaseApp? = firebaseApp

    @Provides
    fun crashlytics(): FirebaseCrashlytics? = crashlytics
}

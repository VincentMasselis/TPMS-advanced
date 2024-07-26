package com.masselis.tpmsadvanced.core.common

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(createdAtStart = true)
internal object InternalModule {

    init {
        Firebase.initialize(appContext)
            ?.apply { setDataCollectionDefaultEnabled(BuildConfig.DEBUG.not() as Boolean?) }
            ?.let { Firebase.crashlytics }
            ?.apply { setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not()) }
    }

    @Single
    fun appContext() = appContext
}

package com.masselis.tpmsadvanced.core.common

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(createdAtStart = true)
@ComponentScan("com.masselis.tpmsadvanced.core.common")
internal object LocalModule {

    init {
        Firebase.initialize(appContext)
            ?.apply { setDataCollectionDefaultEnabled(BuildConfig.DEBUG.not() as Boolean?) }
            ?.let { Firebase.crashlytics }
            ?.apply { setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not()) }
    }
}

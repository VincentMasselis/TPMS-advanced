package com.masselis.tpmsadvanced.core.common

import android.annotation.SuppressLint
import android.content.Context

public lateinit var appGraph: Any

@SuppressLint("StaticFieldLeak")
private lateinit var privateContext: Context
public val appContext: Context get() = privateContext

@Suppress("unused")
public class CoreCommonInitializer : AppGraphReadyInitializer<Context> {
    override fun create(context: Context): Context {
        privateContext = context
        with(Bindings.coreCommonInternal) {
            // Forces FirebaseApp and crashlytics to be initialized
            firebaseApp()?.isDataCollectionDefaultEnabled = BuildConfig.DEBUG.not()
            crashlytics()?.isCrashlyticsCollectionEnabled = BuildConfig.DEBUG.not()
        }
        return context
    }

}

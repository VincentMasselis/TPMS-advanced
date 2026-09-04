package com.masselis.tpmsadvanced.core.common

import android.annotation.SuppressLint
import android.content.Context
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity.Info
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import com.masselis.tpmsadvanced.core.common.BuildConfig.DEBUG

public lateinit var appGraph: Any

@SuppressLint("StaticFieldLeak")
private lateinit var privateContext: Context
public val appContext: Context get() = privateContext

@Suppress("unused", "OPT_IN_USAGE")
public class CoreCommonInitializer : AppGraphReadyInitializer<Context> {
    override fun create(context: Context): Context {
        privateContext = context
        // Clears the default list of writers (logcat output is used by default)
        Logger.setLogWriters(emptyList())
        if (DEBUG.not()) {
            // Default severity is "Verbose", for production build, the minimal severity is higher
            Logger.setMinSeverity(Info)
        }
        if (DEBUG) {
            // Enable logcat output while debugging
            Logger.addLogWriter(LogcatWriter())
        }

        with(Bindings.coreCommonInternal) {
            // Forces FirebaseApp and crashlytics to be initialized
            firebaseApp()?.isDataCollectionDefaultEnabled = DEBUG.not()
            crashlytics()?.isCrashlyticsCollectionEnabled = DEBUG.not()

            if (crashlytics()?.isCrashlyticsCollectionEnabled == true) {
                // If Crashlytics is enabled, link it to Kermit
                Logger.addLogWriter(CrashlyticsLogWriter())
            }
        }
        return context
    }

}

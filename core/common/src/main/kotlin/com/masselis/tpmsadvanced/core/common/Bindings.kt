package com.masselis.tpmsadvanced.core.common

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

@Suppress("unused")
@ContributesTo(AppScope::class)
public interface Bindings {

    @Provides
    private fun firebaseApp(context: Context): FirebaseApp? = Firebase.initialize(context)

    @Provides
    private fun crashlytics(firebaseApp: FirebaseApp?): FirebaseCrashlytics? =
        firebaseApp?.let { Firebase.crashlytics }

    @Provides
    private fun context(): Context = appContext

    public val coreCommonInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val firebaseApp: () -> FirebaseApp?,
        internal val crashlytics: () -> FirebaseCrashlytics?
    )

    public companion object : Bindings by appGraph as Bindings
}

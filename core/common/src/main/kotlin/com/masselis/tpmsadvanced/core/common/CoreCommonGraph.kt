package com.masselis.tpmsadvanced.core.common

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraph

public interface CoreCommonGraph {

    public val context: Context

    public companion object : InternalGraph by createGraph<InternalGraph>() {
        init {
            // Forces FirebaseApp and crashlytics to be initialized
            firebaseApp()
            crashlytics()
        }
    }
}

@DependencyGraph
internal interface InternalGraph : CoreCommonGraph {

    @Provides
    private fun firebaseApp(context: Context): FirebaseApp? = Firebase.initialize(context)

    @Provides
    private fun crashlytics(firebaseApp: FirebaseApp?): FirebaseCrashlytics? =
        firebaseApp?.let { Firebase.crashlytics }

    @Provides
    private fun context(): Context = appContext

    fun firebaseApp(): FirebaseApp?

    fun crashlytics(): FirebaseCrashlytics?
}

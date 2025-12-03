package com.masselis.tpmsadvanced.core.common

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraph

public interface CoreCommonComponent {

    public val context: Context

    public companion object : CoreCommonComponent by InternalComponent
}

@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalComponent : CoreCommonComponent {

    fun firebaseApp(): FirebaseApp?

    fun crashlytics(): FirebaseCrashlytics?

    companion object : InternalComponent by createGraph<InternalComponent>() {
        init {
            // Forces FirebaseApp and crashlytics to be initialized
            firebaseApp()
            crashlytics()
        }
    }
}

@Suppress("unused")
@BindingContainer
private object Bindings {
    @Provides
    private fun firebaseApp(context: Context): FirebaseApp? = Firebase.initialize(context)

    @Provides
    private fun crashlytics(firebaseApp: FirebaseApp?): FirebaseCrashlytics? =
        firebaseApp?.let { Firebase.crashlytics }

    @Provides
    private fun context(): Context = appContext
}

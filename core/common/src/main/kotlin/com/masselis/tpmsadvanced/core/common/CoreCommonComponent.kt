package com.masselis.tpmsadvanced.core.common

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.BindsInstance
import dagger.Component

public interface CoreCommonComponent {

    public val context: Context

    public companion object : CoreCommonComponent by InternalComponent
}

@Component(
    modules = [
        FirebaseModule::class
    ]
)
internal interface InternalComponent : CoreCommonComponent {

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance context: Context = appContext): InternalComponent
    }

    val firebaseApp: FirebaseApp?
    val crashlytics: FirebaseCrashlytics?

    companion object : InternalComponent by DaggerInternalComponent.factory().build() {
        init {
            // Forces FirebaseApp to be initialized
            firebaseApp
            // Forces FirebaseCrashlytics to be initialized
            crashlytics
        }
    }
}

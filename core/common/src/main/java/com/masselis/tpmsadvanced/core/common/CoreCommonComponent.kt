package com.masselis.tpmsadvanced.core.common

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        FirebaseModule::class
    ]
)
public abstract class CoreCommonComponent {

    @Component.Factory
    internal abstract class Factory {
        abstract fun build(@BindsInstance context: Context): CoreCommonComponent
    }

    public abstract val context: Context
    public abstract val firebaseApp: FirebaseApp?
    public abstract val crashlytics: FirebaseCrashlytics?
}

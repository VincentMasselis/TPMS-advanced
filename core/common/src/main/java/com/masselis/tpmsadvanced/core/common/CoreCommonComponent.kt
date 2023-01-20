package com.masselis.tpmsadvanced.core.common

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.BindsInstance
import dagger.Component
import javax.inject.Inject

@Component(
    modules = [
        FirebaseModule::class
    ]
)
public interface CoreCommonComponent {

    @Component.Factory
    public interface Factory {
        public fun build(@BindsInstance context: Context): CoreCommonComponent
    }

    public val context: Context

    public fun inject(injectable: Injectable)

    public companion object : Injectable()

    @Suppress("unused")
    public abstract class Injectable protected constructor() :
        CoreCommonComponent by DaggerCoreCommonComponent
            .factory()
            .build(appContext) {

        @set:Inject
        internal var firebaseApp: FirebaseApp? = null

        @set:Inject
        internal var crashlytics: FirebaseCrashlytics? = null

        init {
            @Suppress("LeakingThis")
            inject(this)
        }
    }
}

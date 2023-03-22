package com.masselis.tpmsadvanced.interfaces.usecase

import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

internal class NoveltyUseCase @Inject constructor(
    private val appPreferences: AppPreferences
) {

    private val shouldShowCarKindShown by lazy { AtomicBoolean(false) }

    fun consumeShowCarKind(): Boolean = with(appPreferences) {
        // Current version targets the right version code
        runningVersionCode == 1020L
                // Current apk is an update
                && isFreshInstallation.not()
                // This session was started for the first time since the update
                && (previousVersionCode ?: 0) < runningVersionCode
                // The current session shows the novelty only once
                && shouldShowCarKindShown.compareAndSet(false, true)
    }
}
package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.core.common.isRunningInstrumentedTest
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

public class NoveltyUseCase internal constructor(
    private val appPreferences: AppPreferences
) {

    private val shownSet = mutableSetOf<String>()
    private val lock = ReentrantLock()

    /**
     * @return `true` if you can show the novelty, `false` otherwise
     */
    public fun consume(name: String, targetVcRange: LongRange): Boolean = with(appPreferences) {
        // When running instrumented test, I don't want to show any contextual overlay
        isRunningInstrumentedTest.not()
                // Current version targets the right version code
                && currentVersionCode in targetVcRange
                // Current apk is an update
                && isFreshInstallation.not()
                // This session was started for the first time since the update
                && (previousVersionCode ?: 0) < currentVersionCode
                // The current session shows the novelty only once
                && lock.withLock { shownSet.add(name) }
    }
}

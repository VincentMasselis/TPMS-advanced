package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.common.isRunningInstrumentedTest
import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import okio.withLock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

@FeatureCoreComponent.Scope
public class NoveltyUseCase @Inject constructor(
    private val appPreferences: AppPreferences
) {

    private val shownSet = mutableSetOf<String>()
    private val lock = ReentrantLock()

    /**
     * @return `true` if you can show the novelty, `false` otherwise
     */
    public fun consume(name: String, targetVc: Long): Boolean = with(appPreferences) {
        // When running instrumented test, I don't want to show any contextual overlay
        isRunningInstrumentedTest.not()
                // Current version targets the right version code
                && runningVersionCode == targetVc
                // Current apk is an update
                && isFreshInstallation.not()
                // This session was started for the first time since the update
                && (previousVersionCode ?: 0) < runningVersionCode
                // The current session shows the novelty only once
                && lock.withLock { shownSet.add(name) }
    }
}

package com.masselis.tpmsadvanced.gitflow.valuesource

import StricSemanticVersion
import com.masselis.tpmsadvanced.gitflow.valuesource.VersionCode.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.jetbrains.annotations.VisibleForTesting

internal abstract class VersionCode : ValueSource<Int, Parameters> {

    interface Parameters : ValueSourceParameters {
        val version: Property<StricSemanticVersion>
        val currentBranch: Property<String>
        val releaseBranch: Property<String>
        val mainBranch: Property<String>
        val releaseBuildCount: Property<Int>
    }

    override fun obtain(): Int = compute(
        version = parameters.version.get(),
        currentBranch = parameters.currentBranch.get(),
        releaseBranch = parameters.releaseBranch.get(),
        mainBranch = parameters.mainBranch.get(),
        releaseBuildCount = parameters.releaseBuildCount.get(),
    )
    companion object {

        /**
         * Pure formula extracted out of the `VersionCode` `ValueSource` so it's unit-testable without
         * Gradle. Kept byte-identical to the original: `major*1_00_00_000 + minor*1_00_000 + patch*1_000 +
         * suffix`, suffix being `999` on main (guarantees main's build for a version is always the
         * highest versionCode for that version), the release-branch build count on the release branch
         * (increments per push), or `0` anywhere else. Play Store's versionCode max is `210_00_00_000`.
         */
        @VisibleForTesting
        fun compute(
            version: StricSemanticVersion,
            currentBranch: String,
            releaseBranch: String,
            mainBranch: String,
            releaseBuildCount: Int,
        ): Int = version.major.times(1_00_00_000)
            .plus(version.minor.times(1_00_000))
            .plus(version.patch.times(1_000))
            .plus(
                when (currentBranch) {
                    mainBranch -> 999
                    releaseBranch -> releaseBuildCount
                    else -> 0
                }
            )
    }
}
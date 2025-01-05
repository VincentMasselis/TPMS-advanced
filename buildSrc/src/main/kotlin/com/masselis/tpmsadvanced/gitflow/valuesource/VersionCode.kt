package com.masselis.tpmsadvanced.gitflow.valuesource

import StricSemanticVersion
import com.masselis.tpmsadvanced.gitflow.valuesource.VersionCode.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

internal abstract class VersionCode : ValueSource<Int, Parameters> {

    interface Parameters : ValueSourceParameters {
        val version: Property<StricSemanticVersion>
        val currentBranch: Property<String>
        val releaseBranch: Property<String>
        val mainBranch: Property<String>
        val releaseBuildCount: Property<Int>
    }

    // Max value for the Play Store is: 210_00_00_000
    override fun obtain(): Int = parameters
        .version
        .get()
        .let { version ->
            version.major.times(1_00_00_000)
                .plus(version.minor.times(1_00_000))
                .plus(version.patch.times(1_000))
                .plus(
                    when (parameters.currentBranch.get()) {
                        parameters.mainBranch.get() -> 999
                        parameters.releaseBranch.get() -> parameters.releaseBuildCount.get()
                        else -> 0
                    }
                ).plus(13)
        }
}
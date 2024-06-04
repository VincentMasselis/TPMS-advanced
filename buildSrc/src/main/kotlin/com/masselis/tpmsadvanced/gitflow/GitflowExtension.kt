package com.masselis.tpmsadvanced.gitflow

import SemanticVersion
import StricSemanticVersion
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

public abstract class GitflowExtension(
    public val currentReleaseTag: Provider<SemanticVersion>,
    public val lastReleaseCommitSha: Provider<String>,
) {
    public abstract val version: Property<StricSemanticVersion>
    public abstract val developBranch: Property<String>
    public abstract val releaseBranch: Property<String>
    public abstract val hotfixBranch: Property<String>
    public abstract val mainBranch: Property<String>
}

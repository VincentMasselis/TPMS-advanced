package com.masselis.tpmsadvanced.github

import SemanticVersion
import org.gradle.api.provider.Property

public interface GithubExtension {
    public val githubToken: Property<String>
    public val currentReleaseTag: Property<SemanticVersion>
    public val lastReleaseCommitSha: Property<String>

    /** Branch merged from by the automatic back-merge pull request, e.g. `main`. */
    public val backMergeSource: Property<String>

    /** Branch merged into by the automatic back-merge pull request, e.g. `develop`. */
    public val backMergeTarget: Property<String>
}
package com.masselis.tpmsadvanced.github

import SemanticVersion
import org.gradle.api.provider.Property

public interface GithubExtension {
    public val githubToken: Property<String>
    public val currentReleaseTag: Property<SemanticVersion>
    public val lastReleaseCommitSha: Property<String>
}
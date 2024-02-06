package com.masselis.tpmsadvanced.github

import org.gradle.api.provider.Property

public interface GithubExtension {
    public val githubToken: Property<String>
    public val currentReleaseTag: Property<String>
    public val lastReleaseCommitSha: Property<String>
}
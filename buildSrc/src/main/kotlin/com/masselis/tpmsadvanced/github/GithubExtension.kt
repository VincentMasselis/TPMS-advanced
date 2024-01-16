package com.masselis.tpmsadvanced.github

import org.gradle.api.provider.Property

public interface GithubExtension {
    public val githubToken: Property<String>
}
package com.masselis.tpmsadvanced.github

import SemanticVersion
import org.gradle.api.provider.Property

public interface GithubExtension {
    public val versionName: Property<SemanticVersion>
    public val githubToken: Property<String>
    public val prodBranch: Property<String>
    public val betaBranch: Property<String>
}
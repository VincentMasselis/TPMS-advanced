package com.masselis.tpmsadvanced.gitflow

import SemanticVersion
import org.gradle.api.provider.Property

public interface GitflowExtension {
    public val versionName: Property<SemanticVersion>
    public val developBranch: Property<String>
    public val releaseBranch: Property<String>
    public val hotfixBranch: Property<String>
    public val mainBranch: Property<String>
}

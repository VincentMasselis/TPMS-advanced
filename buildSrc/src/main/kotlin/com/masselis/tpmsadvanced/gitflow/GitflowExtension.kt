package com.masselis.tpmsadvanced.gitflow

import SemanticVersion
import StricSemanticVersion
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

public abstract class GitflowExtension internal constructor(
    public val currentReleaseTag: Provider<SemanticVersion>,
    public val lastReleaseCommitSha: Provider<String>,
) {
    public abstract val version: Property<StricSemanticVersion>

    /** The git remote everything is validated/pushed against, e.g. `origin`. */
    public abstract val remote: Property<String>

    /** Local branch name, e.g. `develop` - never a remote-tracking ref such as `origin/develop`. */
    public abstract val developBranch: Property<String>

    /** Local branch name, e.g. `main` - never a remote-tracking ref such as `origin/main`. */
    public abstract val mainBranch: Property<String>

    /**
     * Local branch name for the release currently being cut, derived from [version] as
     * `release/<version>` - not independently settable, so it can never drift from the version it
     * names.
     */
    public val releaseBranch: Provider<String> = version.map { "release/$it" }

    /** Same as [releaseBranch], for hotfixes: `hotfix/<version>`. */
    public val hotfixBranch: Provider<String> = version.map { "hotfix/$it" }

    /** Directory holding the Play Store release notes, one `<version>.txt` file per release. */
    public abstract val releaseNotesDir: DirectoryProperty

    init {
        remote.convention("origin")
        developBranch.convention("develop")
        mainBranch.convention("main")
    }
}

package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.version.BumpType
import com.masselis.tpmsadvanced.gitflow.version.BumpType.Companion.bump
import com.masselis.tpmsadvanced.gitflow.version.TpmsToml
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Bumps `gradle/libs.versions.toml`'s `app` version and commits the change. Always lands the
 * commit on whichever branch is currently checked out - the caller is expected to run this right
 * after checking out the new release/hotfix branch, never on `develop`: keeping `develop`
 * untouched is what makes the automatic main-to-develop back-merge conflict-free.
 */
internal abstract class BumpVersionAndCommit : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:InputFile
    abstract val versionCatalog: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val bumpType: Property<BumpType>

    init {
        group = "gitflow"
        description = "Bumps the app version in the version catalog and commits the change"
    }

    @TaskAction
    internal fun process() {
        val bumpType = bumpType.orNull ?: throw GradleException("$name requires -Pgitflow.bump=major|minor|patch")
        val toml = TpmsToml(versionCatalog.get().asFile)
        val current = toml.appVersion
        val next = current.bump(bumpType).also { toml.appVersion = it }
        execOperations.exec { commandLine("git", "add", versionCatalog.get().asFile.absolutePath) }
        execOperations.exec { commandLine("git", "commit", "-m", "chore(gitflow): bump version to $next") }
        logger.lifecycle("Bumped version {} -> {}", current, next)
    }
}

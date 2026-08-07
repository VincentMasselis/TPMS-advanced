package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Freshens local remote-tracking refs and tags before cutting a release/hotfix locally. Only
 * `createRelease`/`createHotfix` depend on this - CI's validation tasks (`assertReleaseBranchIsValid`
 * etc.) deliberately don't, since `actions/checkout` with `fetch-depth: 0` already fetched
 * everything before Gradle even started, and adding a network call to that critical path would
 * only add a new flake source for no benefit.
 */
internal abstract class FetchGitRefs : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val remote: Property<String>

    init {
        group = "gitflow"
        description = "Freshens local remote-tracking refs and tags"
    }

    @TaskAction
    internal fun process() {
        execOperations.exec { commandLine("git", "fetch", "--prune", "--tags", remote.get()) }
    }
}

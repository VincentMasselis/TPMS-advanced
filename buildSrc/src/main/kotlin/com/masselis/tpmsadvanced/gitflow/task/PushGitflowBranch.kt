package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Pushes the current branch and sets it to track [remote]. A separate task from
 * `createRelease`/`createHotfix`, so a human reviewing locally can inspect the bump commit before
 * pushing, or run both together (e.g. `createRelease pushGitflowBranch -Pgitflow.bump=minor`) in
 * one invocation.
 */
internal abstract class PushGitflowBranch : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val remote: Property<String>

    init {
        group = "gitflow"
        description = "Pushes the current branch and sets it to track the remote"
    }

    @TaskAction
    internal fun process() {
        execOperations.exec { commandLine("git", "push", "--set-upstream", remote.get(), "HEAD") }
    }
}
